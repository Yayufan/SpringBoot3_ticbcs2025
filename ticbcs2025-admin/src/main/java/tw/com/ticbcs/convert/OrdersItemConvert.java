package tw.com.ticbcs.convert;

import java.util.List;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddOrdersItemDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutOrdersItemDTO;
import tw.com.ticbcs.pojo.VO.OrdersItemVO;
import tw.com.ticbcs.pojo.entity.OrdersItem;

@Mapper(componentModel = "spring")
public interface OrdersItemConvert {

	OrdersItem addDTOToEntity(AddOrdersItemDTO addOrdersItemDTO);

	OrdersItem putDTOToEntity(PutOrdersItemDTO putOrdersItemDTO);
	
	OrdersItemVO entityToVO(OrdersItem ordersItem);
	
	List<OrdersItemVO> entityListToVOList(List<OrdersItem> ordersItemList);
	
}
