package com.abranlezama.ecommerceservice.service.imp;

import com.abranlezama.ecommerceservice.exception.EmptyOrderException;
import com.abranlezama.ecommerceservice.exception.ExceptionMessages;
import com.abranlezama.ecommerceservice.model.*;
import com.abranlezama.ecommerceservice.objectmother.*;
import com.abranlezama.ecommerceservice.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = { "stripe.api.key=value" })
class OrderServiceImpTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CustomerOrderRepository customerOrderRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private OrderServiceImp cut;
    @Captor
    ArgumentCaptor<Cart> cartArgumentCaptor;

    @Test
    void shouldCreateStripeCheckoutSession() throws StripeException {
        // Given
        User user = UserMother.user().id(1L).build();
        Product product = ProductMother.saveProduct().id(1L).build();
        CartItem cartItem = CartItemMother.cartItem().product(product).build();
        Cart cart = CartMother.cart()
                .cartItems(List.of(cartItem))
                .totalCost(500F)
                .build();

        given(cartRepository.findByCustomer_User_Id(user.getId())).willReturn(cart);
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when((MockedStatic.Verification) Session.create(anyMap())).thenReturn(new Session());

            // When
            Session session = cut.createSession(user);

        }
    }

    @Test
    void shouldCreateOrderFromCartItems() {
        // Given
        Product product = ProductMother.saveProduct().id(1L).build();
        CustomerOrder customerOrder = CustomerOrder.builder().id(1L).build();
        CartItem cartItem = CartItemMother.cartItem().product(product).build();
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        User user = UserMother.user().id(1L).build();
        Customer customer = CustomerMother.customer().id(1L).user(user).build();
        Cart cart = CartMother.cart().cartItems(cartItems).totalCost(500F).customer(customer)
                .build();

        given(cartRepository.findByCustomer_User_Id(user.getId())).willReturn(cart);
        given(orderStatusRepository.findByStatus(OrderStatusType.PROCESSING)).willReturn(Optional.of(new OrderStatus()));
        given(customerOrderRepository.save(any())).willReturn(customerOrder);


        // When
        cut.createOrder(user.getId());

        // Then
        then(cartItemRepository).should().deleteAll(cartItems);
        then(cartRepository).should().save(cartArgumentCaptor.capture());
        assertThat(cartArgumentCaptor.getValue().getTotalCost()).isEqualTo(0);
        assertThat(cartArgumentCaptor.getValue().getCartItems().size()).isEqualTo(0);
    }

    @Test
    void shouldThrowEmptyCartExceptionWhenCreatingOrderWithNoItems() {
        // Given
        Cart cart = CartMother.cart().totalCost(0F).cartItems(List.of()).build();
        long userId = 1L;

        given(cartRepository.findByCustomer_User_Id(userId)).willReturn(cart);

        // When
        assertThatThrownBy(() -> cut.createOrder(userId))
                .hasMessage(ExceptionMessages.EMPTY_ORDER)
                .isInstanceOf(EmptyOrderException.class);

        // Then
        then(orderStatusRepository).shouldHaveNoInteractions();
    }

}