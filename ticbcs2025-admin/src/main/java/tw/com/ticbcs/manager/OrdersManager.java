package tw.com.ticbcs.manager;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.enums.OrderStatusEnum;
import tw.com.ticbcs.mapper.OrdersMapper;
import tw.com.ticbcs.pojo.entity.Orders;

@Component
@RequiredArgsConstructor
public class OrdersManager {

	private static final String ITEMS_SUMMARY_REGISTRATION = "Registration Fee";
	private final OrdersMapper ordersMapper;

	
	public Orders createZeroAmountRegistrationOrder(Long memberId) {
		//此為0元訂單
		BigDecimal amount = BigDecimal.ZERO;

		//創建繳費完成的註冊費訂單
		Orders orders = new Orders();
		orders.setMemberId(memberId);
		orders.setItemsSummary(ITEMS_SUMMARY_REGISTRATION);
		orders.setStatus(OrderStatusEnum.PAYMENT_SUCCESS.getValue());
		orders.setTotalAmount(amount);

		// 資料庫新增
		ordersMapper.insert(orders);

		return orders;

	}

}
