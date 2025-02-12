package com.example.project_economic.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.project_economic.dto.request.CartItemRequest;
import com.example.project_economic.dto.response.CartItemResponse;
import com.example.project_economic.entity.CartItemEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.impl.CartItemServiceImpl;
import com.example.project_economic.mapper.CartItemMapper;
import com.example.project_economic.repository.CartItemRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class CartItemServiceTest {
	@Mock
	CartItemRepository cartItemRepositoryMock;

	@Mock
	UserRepository userRepositoryMock;

	@Mock
	ProductDetailRepository productDetailRepositoryMock;

	@Mock
	CartItemMapper cartItemMapperMock;

	@Mock
	SecurityContext securityContextMock;

	@Mock
	Authentication authenticationMock;

	@InjectMocks
	CartItemServiceImpl cartItemService;

	// Request
	CartItemRequest cartItemRequest;
	// Response
	CartItemEntity cartItemEntity1;
	CartItemEntity cartItemEntity2;
	List<CartItemEntity> cartItemEntityList;
	CartItemResponse cartItemResponse1;
	CartItemResponse cartItemResponse2;
	UserEntity userEntity;
	ProductDetailEntity productDetailEntity;
	Jwt accessJwt;

	private static final Long USER_ID = 1L;
	;
	private static final Long CART_SIZE = 2L;
	private static final Long PRODUCT_DETAIL_ID = 1L;
	private static final Long CART_ITEM_ID = 1L;
	private static final Integer CART_ITEM_REQUEST_QUANTITY = 2;
	private static final Integer CART_ITEM_ENTITY_QUANTITY = 3;
	private static final String ACCESS_TOKEN_REQUEST = "accessTokenRequest";

	@BeforeEach
	void initData() {
		// Request
		cartItemRequest = CartItemRequest.builder()
				.id(CART_ITEM_ID)
				.userId(USER_ID)
				.productDetailId(PRODUCT_DETAIL_ID)
				.quantity(CART_ITEM_REQUEST_QUANTITY)
				.build();
		// Response
		cartItemEntity1 =
				CartItemEntity.builder().quantity(CART_ITEM_ENTITY_QUANTITY).build();
		cartItemEntity2 = new CartItemEntity();
		cartItemEntityList = Arrays.asList(cartItemEntity1, cartItemEntity2);

		cartItemResponse1 = new CartItemResponse();
		cartItemResponse2 = new CartItemResponse();

		userEntity = new UserEntity();
		productDetailEntity = new ProductDetailEntity();

		accessJwt = Jwt.withTokenValue(ACCESS_TOKEN_REQUEST)
				.headers(h -> h.put("alg", "HS512"))
				.claim("uid", USER_ID)
				.build();
	}

	@Test
	void getAllByUserId_validRequest_success() {
		// GIVEN
		when(cartItemRepositoryMock.findAllByUserId(USER_ID)).thenReturn(cartItemEntityList);
		when(cartItemMapperMock.toCartItemResponse(cartItemEntity1)).thenReturn(cartItemResponse1);
		when(cartItemMapperMock.toCartItemResponse(cartItemEntity2)).thenReturn(cartItemResponse2);
		// WHEN
		List<CartItemResponse> result = cartItemService.getAllByUserId(USER_ID);
		// THEN
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(cartItemResponse1));
		assertTrue(result.contains(cartItemResponse2));

		verify(cartItemRepositoryMock, times(1)).findAllByUserId(USER_ID);
		verify(cartItemMapperMock, times(2)).toCartItemResponse(any(CartItemEntity.class));
	}

	@Test
	void countAllByUserId_validRequest_success() {
		// GIVEN
		when(cartItemRepositoryMock.countAllByUserId(USER_ID)).thenReturn(CART_SIZE);
		// WHEN
		Long result = cartItemService.countAllByUserId(USER_ID);
		// THEN
		assertNotNull(result);
		assertEquals(CART_SIZE, result);

		verify(cartItemRepositoryMock).countAllByUserId(USER_ID);
	}

	@Test
	void add_existsCartItem_successCartItemUpdated() {
		// GIVEN
		when(cartItemRepositoryMock.findFirstByUserIdAndProductDetailId(USER_ID, PRODUCT_DETAIL_ID))
				.thenReturn(cartItemEntity1);
		when(cartItemRepositoryMock.save(cartItemEntity1)).thenReturn(cartItemEntity1);
		when(cartItemMapperMock.toCartItemResponse(cartItemEntity1)).thenReturn(cartItemResponse1);
		// WHEN
		CartItemResponse result = cartItemService.add(cartItemRequest);
		// THEN
		assertNotNull(result);
		assertSame(cartItemResponse1, result);

		verify(cartItemRepositoryMock).findFirstByUserIdAndProductDetailId(USER_ID, PRODUCT_DETAIL_ID);
		verify(cartItemRepositoryMock).save(cartItemEntity1);
		verify(cartItemMapperMock).toCartItemResponse(cartItemEntity1);
		verifyNoInteractions(userRepositoryMock, productDetailRepositoryMock);
	}

	@Test
	void add_newCartItem_successCartItemAdded() {
		// GIVEN
		when(cartItemRepositoryMock.findFirstByUserIdAndProductDetailId(USER_ID, PRODUCT_DETAIL_ID))
				.thenReturn(null);
		when(userRepositoryMock.getReferenceById(USER_ID)).thenReturn(userEntity);
		when(productDetailRepositoryMock.getReferenceById(PRODUCT_DETAIL_ID)).thenReturn(productDetailEntity);
		when(cartItemRepositoryMock.save(any(CartItemEntity.class))).thenReturn(cartItemEntity2);
		when(cartItemMapperMock.toCartItemResponse(cartItemEntity2)).thenReturn(cartItemResponse2);
		// WHEN
		CartItemResponse result = cartItemService.add(cartItemRequest);
		// THEN
		assertNotNull(result);
		assertSame(cartItemResponse2, result);

		verify(cartItemRepositoryMock).findFirstByUserIdAndProductDetailId(USER_ID, PRODUCT_DETAIL_ID);
		verify(userRepositoryMock).getReferenceById(USER_ID);
		verify(productDetailRepositoryMock).getReferenceById(PRODUCT_DETAIL_ID);
		verify(cartItemRepositoryMock).save(any(CartItemEntity.class));
		verify(cartItemMapperMock).toCartItemResponse(cartItemEntity2);
	}

	@Test
	void update_validRequest_successCartItemUpdated() {
		// GIVEN
		when(cartItemRepositoryMock.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItemEntity1));
		when(cartItemRepositoryMock.save(cartItemEntity1)).thenReturn(cartItemEntity1);
		when(cartItemMapperMock.toCartItemResponse(cartItemEntity1)).thenReturn(cartItemResponse1);
		// WHEN
		CartItemResponse result = cartItemService.update(cartItemRequest);
		// THEN
		assertNotNull(result);
		assertSame(cartItemResponse1, result);

		verify(cartItemRepositoryMock).findById(CART_ITEM_ID);
		verify(cartItemRepositoryMock).save(cartItemEntity1);
		verify(cartItemMapperMock).toCartItemResponse(cartItemEntity1);
	}

	@Test
	void update_cartItemNotFound_throwsException() {
		// GIVEN
		when(cartItemRepositoryMock.findById(CART_ITEM_ID)).thenReturn(Optional.empty());
		// WHEN
		AppException exception = assertThrows(AppException.class, () -> cartItemService.update(cartItemRequest));
		// THEN
		assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());

		verify(cartItemRepositoryMock).findById(CART_ITEM_ID);
		verifyNoInteractions(cartItemMapperMock);
	}

	@Test
	void delete_validRequest_successCartItemDeleted() {
		// GIVEN
		when(cartItemRepositoryMock.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItemEntity1));
		// WHEN
		Long result = cartItemService.delete(CART_ITEM_ID);
		// THEN
		assertNotNull(result);
		assertEquals(CART_ITEM_ID, result);

		verify(cartItemRepositoryMock).findById(CART_ITEM_ID);
		verify(cartItemRepositoryMock).deleteById(CART_ITEM_ID);
	}

	@Test
	void delete_cartItemNotFound_throwsException() {
		// GIVEN
		when(cartItemRepositoryMock.findById(CART_ITEM_ID)).thenReturn(Optional.empty());
		// WHEN
		AppException exception = assertThrows(AppException.class, () -> cartItemService.delete(CART_ITEM_ID));
		// THEN
		assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());

		verify(cartItemRepositoryMock).findById(CART_ITEM_ID);
	}

	@Test
	void deleteAllMyCart_validRequest_success() {
		// GIVEN
		SecurityContextHolder.setContext(securityContextMock);
		when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
		when(authenticationMock.getPrincipal()).thenReturn(accessJwt);
		// WHEN
		Long result = cartItemService.deleteAllMyCart();
		// THEN
		assertNotNull(result);
		assertEquals(USER_ID, result);

		verify(securityContextMock).getAuthentication();
		verify(authenticationMock).getPrincipal();
		verify(cartItemRepositoryMock).deleteAllByUserId(USER_ID);
	}
}
