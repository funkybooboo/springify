package com.funkybooboo.store.carts.services;

import com.funkybooboo.store.carts.repositories.CartRepository;
import com.funkybooboo.store.carts.dtos.responses.CartResponseDto;
import com.funkybooboo.store.carts.dtos.responses.CartItemResponseDto;
import com.funkybooboo.store.carts.entites.Cart;
import com.funkybooboo.store.carts.exceptions.CartNotFoundException;
import com.funkybooboo.store.carts.mappers.CartMapper;
import com.funkybooboo.store.products.exceptions.ProductNotFoundException;
import com.funkybooboo.store.products.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartResponseDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toResponseDto(cart);
    }
    
    public CartItemResponseDto addToCart(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toResponseDto(cartItem);
    }
    
    public CartResponseDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        return cartMapper.toResponseDto(cart);
    }
    
    public CartItemResponseDto updateItem(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartItem = cart.getItem(productId);

        if (cartItem == null) {
            throw new ProductNotFoundException();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cartItem);
    }
    
    public void removeItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeItem(productId);
        cartRepository.save(cart);
    }
    
    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
