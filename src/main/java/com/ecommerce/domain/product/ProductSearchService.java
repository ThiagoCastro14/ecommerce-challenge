package com.ecommerce.domain.product;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.ecommerce.search.product.ProductDocument;
import com.ecommerce.search.product.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository searchRepository;

    public Page<ProductDocument> search(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size
    ) {
        List<Query> must = new ArrayList<>();
        List<Query> filters = new ArrayList<>();

        // Estoque > 0
        filters.add(Query.of(q -> q.range(r -> r.field("stockQuantity").gt(JsonData.of(0)))));

        // Nome (fuzzy)
        if (name != null && !name.isBlank()) {
            must.add(Query.of(q -> q.match(m -> m.field("name").query(name).fuzziness("AUTO"))));
        }

        // Categoria (exata)
        if (category != null && !category.isBlank()) {
            filters.add(Query.of(q -> q.term(t -> t.field("category.keyword").value(category))));
        }

        // Preço
        if (minPrice != null || maxPrice != null) {
            RangeQuery.Builder range = new RangeQuery.Builder().field("price");
            if (minPrice != null) range.gte(JsonData.of(minPrice));
            if (maxPrice != null) range.lte(JsonData.of(maxPrice));
            filters.add(Query.of(q -> q.range(range.build())));
        }

        // Query final
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        if (must.isEmpty()) boolBuilder.must(Query.of(q -> q.matchAll(m -> m)));
        else must.forEach(boolBuilder::must);
        filters.forEach(boolBuilder::filter);

        Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));

        var nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        // Executa via ElasticsearchOperations
        SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

        List<ProductDocument> docs = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(docs, PageRequest.of(page, size), hits.getTotalHits());
    }
}
