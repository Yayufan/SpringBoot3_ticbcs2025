package tw.com.ticbcs.convert;

import java.util.List;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddOrdersDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutOrdersDTO;
import tw.com.ticbcs.pojo.VO.OrdersVO;
import tw.com.ticbcs.pojo.entity.Orders;

@Mapper(componentModel = "spring")
public interface OrdersConvert {

	Orders addDTOToEntity(AddOrdersDTO addOrdersDTO);

	Orders putDTOToEntity(PutOrdersDTO putOrdersDTO);
	
	OrdersVO entityToVO(Orders orders);
	
	List<OrdersVO> entityListToVOList(List<Orders> ordersList);
	
}
