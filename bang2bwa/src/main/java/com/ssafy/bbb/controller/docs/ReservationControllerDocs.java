package com.ssafy.bbb.controller.docs;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.LocationDto;
import com.ssafy.bbb.model.dto.RejectReasonDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reservation API", description = "부동산 예약, 승인, 취소, 노쇼 신고 관련 컨트롤러")
public interface ReservationControllerDocs {
	
	@Operation(summary = "예약 요청", description = "예약자가 매물에 대한 예약 및 보증금 가승인을 요청합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "예약 신청 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(
									value ="""
											{
											    "success": "SUCCESS",
											    "message": "예약이 완료되었습니다.",
											    "data": null
											}
											"""
							)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "없는 매물 정보 신청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(
									value ="""
											{
											    "success": "FAIL",
											    "message": "존재하지 않는 매물입니다.",
											    "data": null
											}
											"""
							)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "409",
					description = "잘못된 매물 정보 신청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(
									value ="""
											{
											    "success": "FAIL",
											    "message": "이미 예약 진행 중이거나 거래 완료된 매물입니다.",
											    "data": null
											}
											"""
							)
					)
			)
		})
	public ApiResponse<String> createReservation(
			@RequestBody ReservationRequestDto request
			, @Parameter(hidden=true) CustomUserDetails user);

	// 부동산 업자 예약 승인
	@Operation(summary = "예약 승인", description = "중개인이 예약을 승인하고 보증금을 예치합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "예약 승인 성공",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "예약을 승인하였습니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 예약 정보 승인",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 예약정보입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "내 매물이 아닌 예약을 승인",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "자신의 예약에 대해서만 접근 가능합니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "409",
				description = "예약 대기중이 아닌 매물에 대한 예약 승인",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약 대기중인 매물이 아닙니다.",
										    "data": null
										}
										"""
						)
				)
		)
	})
	public ApiResponse<String> acceptReservation(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId
			, @Parameter(hidden=true) CustomUserDetails user);

	@Operation(summary = "예약 거절", description = "중개인이 예약을 거절합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "예약 거절 성공",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "예약을 거절하였습니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 예약 정보에 대한 거절 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 예약정보입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "409",
				description = "PENDING(예약대기) 상태가 아닌 매물에 대한 거절 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약 대기중인 매물이 아닙니다.",
										    "data": null
										}
										"""
						)
				)
		),
	})
	public ApiResponse<String> rejectReservation(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId //
			, @Parameter(hidden=true) CustomUserDetails agent
			, @RequestBody RejectReasonDto rejectResone);

	@Operation(summary = "예약 취소", description = "예약자 사정으로 인해 예약을 취소하고 위약금 정책을 적용합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "예약 취소 성공",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "예약을 취소하였습니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "409",
				description = "PENDING(예약대기), RESERVED(예약중) 상태가 아닌 매물에 대한 취소 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약중인 매물이 아닙니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "자신의 예약이 아닌 예약에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "잘못된 요청입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 예약 정보에 대한 거절 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 예약정보입니다.",
										    "data": null
										}
										"""
						)
				)
		),
	})
	public ApiResponse<String> cancelReservation(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId
			, @Parameter(hidden=true) CustomUserDetails user);

	@Operation(summary = "만남 확인 (위치 인증)", description = "약속 장소 도착 후 만남을 확인합니다. (쌍방 확인 시 정산)")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "예약 매물 근처 도착",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "예약을 확인하였습니다. 상대방의 동의 후 보증금이 반환됩니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 예약 정보에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 예약정보입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "약속 장소와 너무 먼 거리에서 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약 매물과의 거리가 너무 멉니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "자신의 예약이 아닌 예약에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "잘못된 요청입니다.",
										    "data": null
										}
										"""
						)
				)
		)
	})
	public ApiResponse<String> confirmReservation(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId
			, @Parameter(hidden=true) CustomUserDetails user
			, @RequestBody LocationDto curLocation);

	@Operation(summary = "노쇼 신고", description = "상대방이 나타나지 않았을 때 신고합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "상대방이 나타나지 않았을 시, 신고 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "신고가 접수되었습니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "예약 일정 전에 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "노쇼 신고는 예약 일정 이후에만 가능합니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "약속 장소와 너무 먼 거리에서 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약 매물과의 거리가 너무 멉니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 매물 정보에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 매물입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "자신의 예약이 아닌 예약에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "잘못된 요청입니다.",
										    "data": null
										}
										"""
						)
				)
		)
	})
	public ApiResponse<String> reportNoShow(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId
			, @Parameter(hidden=true) CustomUserDetails reporter
			, @RequestBody LocationDto curLocation);

	@Operation(summary = "이의 제기 (노쇼 방어)", description = "허위 신고에 대해 현장 위치를 인증하여 방어합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "억울한 신고에 대한 이의 제기 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "SUCCESS",
										    "message": "매물 근처임이 확인되었습니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 예약 정보에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 예약정보입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "신고된 상태가 아닐 때 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "신고된 상태가 아닙니다!",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "약속 장소와 너무 먼 거리에서 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "예약 매물과의 거리가 너무 멉니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "없는 매물 정보에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "존재하지 않는 매물입니다.",
										    "data": null
										}
										"""
						)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "자신의 예약이 아닌 예약에 대한 요청",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ApiResponse.class),
						examples = @ExampleObject(
								value ="""
										{
										    "success": "FAIL",
										    "message": "잘못된 요청입니다.",
										    "data": null
										}
										"""
						)
				)
		)
	})
	public ApiResponse<String> defendNoShow(
			@Parameter(description = "예약 ID", required=true, example = "100") @PathVariable Long reservationId
			, @Parameter(hidden=true) CustomUserDetails user
			, @RequestBody LocationDto curLocation);
}
