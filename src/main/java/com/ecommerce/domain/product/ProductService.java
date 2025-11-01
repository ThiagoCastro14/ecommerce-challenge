package com.ecommerce.domain.product;

import com.ecommerce.api.dto.ProductDTO;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.search.product.ProductDocument;
import com.ecommerce.search.product.ProductSearchRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper mapper;

    /**
     * Cria um novo produto no MySQL e o replica no Elasticsearch.
     */
    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product product = mapper.toEntity(dto);
        product.setId(null); // garante novo registro no banco relacional
        Product saved = productRepository.save(product);

        // indexa no Elasticsearch
        ProductDocument doc = mapper.toDocument(saved);
        searchRepository.save(doc);

        return mapper.toDTO(saved);
    }

    /**
     * Atualiza um produto existente e sincroniza a atualização no Elasticsearch.
     */
    @Transactional
    public ProductDTO update(UUID id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());

        Product saved = productRepository.save(product);

        // replica a atualização no Elasticsearch
        ProductDocument doc = mapper.toDocument(saved);
        searchRepository.save(doc);

        return mapper.toDTO(saved);
    }

    /**
     * Remove o produto do MySQL e também do Elasticsearch.
     */
    @Transactional
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }

        // remove do MySQL
        productRepository.deleteById(id);

        // remove do Elasticsearch — converte UUID → String
        searchRepository.deleteById(id.toString());
    }

    /**
     * Busca um produto por ID.
     */
    public ProductDTO findById(UUID id) {
        return productRepository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    /**
     * Lista todos os produtos.
     */
        public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
