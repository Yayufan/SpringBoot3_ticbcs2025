package tw.com.ticbcs.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.PaymentConvert;
import tw.com.ticbcs.service.PaymentService;

/**
 * <p>
 * 參加者表，在註冊並實際繳完註冊費後，會進入這張表中，用做之後發送QRcdoe使用 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2025-04-24
 */

@Tag(name = "參加者API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/attendees")
public class AttendeesController {

}
