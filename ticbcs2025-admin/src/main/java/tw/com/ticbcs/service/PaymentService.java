package tw.com.ticbcs.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.ticbcs.pojo.DTO.ECPayDTO.ECPayResponseDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutPaymentDTO;
import tw.com.ticbcs.pojo.entity.Payment;

public interface PaymentService extends IService<Payment> {

	Payment getPayment(Long paymentId);

	List<Payment> getPaymentList();

	IPage<Payment> getPaymentPage(Page<Payment> pageable);

	/**
	 * 這個直接接收綠界returnURL的回傳數據
	 * 
	 * @param ECPayResponseDTO
	 */
	void addPayment(ECPayResponseDTO ECPayResponseDTO);

	void updatePayment(PutPaymentDTO putPaymentDTO);

	void deletePayment(Long paymentId);

	void deletePaymentList(List<Long> paymentIds);

}
