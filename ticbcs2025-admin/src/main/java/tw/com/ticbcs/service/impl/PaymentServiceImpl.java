package tw.com.ticbcs.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.PaymentConvert;
import tw.com.ticbcs.mapper.MemberMapper;
import tw.com.ticbcs.mapper.OrdersMapper;
import tw.com.ticbcs.mapper.PaymentMapper;
import tw.com.ticbcs.pojo.DTO.ECPayDTO.ECPayResponseDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutPaymentDTO;
import tw.com.ticbcs.pojo.entity.Member;
import tw.com.ticbcs.pojo.entity.Orders;
import tw.com.ticbcs.pojo.entity.Payment;
import tw.com.ticbcs.service.OrdersService;
import tw.com.ticbcs.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

	private final PaymentConvert paymentConvert;
	private final OrdersService ordersService;
	private final MemberMapper memberMapper;
	private final OrdersMapper ordersMapper;

	@Override
	public Payment getPayment(Long paymentId) {
		Payment payment = baseMapper.selectById(paymentId);
		return payment;
	}

	@Override
	public List<Payment> getPaymentList() {
		List<Payment> paymentList = baseMapper.selectList(null);
		return paymentList;
	}

	@Override
	public IPage<Payment> getPaymentPage(Page<Payment> page) {
		Page<Payment> paymentPage = baseMapper.selectPage(page, null);
		return paymentPage;
	}

	@Override
	@Transactional
	public void addPayment(ECPayResponseDTO ECPayResponseDTO) {
		// 轉換綠界金流offical Data 轉換 自己這邊的Entity
		Payment payment = paymentConvert.officalDataToEntity(ECPayResponseDTO);
		// 新增響應回來的交易紀錄
		baseMapper.insert(payment);

		// 當前回傳的訂單
		Orders currentOrders;

		// 如果付款成功，更新訂單的付款狀態
		if (payment.getRtnCode().equals("1")) {
			currentOrders = ordersService.getOrders(payment.getOrdersId());
			// 2 代表付款成功，並更新這筆訂單資料
			currentOrders.setStatus(2);
			ordersService.updateById(currentOrders);

		} else {
			currentOrders = ordersService.getOrders(payment.getOrdersId());
			// 3 代表付款失敗，並更新這筆訂單資料
			currentOrders.setStatus(3);
			ordersService.updateById(currentOrders);
		}

		// 3.查詢這個訂單的會員
		Member member = memberMapper.selectById(currentOrders.getMemberId());

		// 4.判斷這個member有沒有group，是否處於團體報名，且付款的更新者為master，從如果有才進行此方法塊
		if (member.getGroupCode() != null && member.getGroupRole() == "master") {

			// 拿到所屬同一個團體報名的會員名單，並且是要group_role 為 slave的成員
			LambdaQueryWrapper<Member> memberQueryWrapper = new LambdaQueryWrapper<>();
			memberQueryWrapper.eq(Member::getGroupCode, member.getGroupCode()).eq(Member::getGroupRole, "slave");
			List<Member> groupMemberList = memberMapper.selectList(memberQueryWrapper);

			for (Member slaveMember : groupMemberList) {
				// 找到memberId為名單內成員且訂單的itemsSummary 為 註冊費的訂單，
				LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
				ordersQueryWrapper.eq(Orders::getMemberId, slaveMember.getMemberId()).eq(Orders::getItemsSummary,
						"Group Registration Fee");

				// 去更新其他slave(子報名者的付款狀態)
				Orders slaveMemberGroupOrder = ordersMapper.selectOne(ordersQueryWrapper);
				slaveMemberGroupOrder.setStatus(currentOrders.getStatus());
				ordersService.updateById(slaveMemberGroupOrder);

			}

		}

	}

	@Override
	public void updatePayment(PutPaymentDTO putPaymentDTO) {
		Payment payment = paymentConvert.putDTOToEntity(putPaymentDTO);
		baseMapper.updateById(payment);

	}

	@Override
	public void deletePayment(Long paymentId) {
		baseMapper.deleteById(paymentId);

	}

	@Override
	public void deletePaymentList(List<Long> paymentIds) {
		baseMapper.deleteBatchIds(paymentIds);
	}

}
