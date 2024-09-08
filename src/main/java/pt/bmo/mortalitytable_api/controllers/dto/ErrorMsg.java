package pt.bmo.mortalitytable_api.controllers.dto;

import lombok.Builder;

@Builder
public record ErrorMsg(long timestamp, String msg) {
}
