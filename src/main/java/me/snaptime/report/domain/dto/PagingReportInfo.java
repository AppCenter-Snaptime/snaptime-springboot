package me.snaptime.report.domain.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PagingReportInfo(

        List<ReportInfo> reportInfoList,
        long totalPages,
        boolean hasNextPage,
        boolean hasPreviousPage
) {
    public static PagingReportInfo of(List<ReportInfo> reportInfoList, long totalPages, boolean hasNextPage, boolean hasPreviousPage)
    {
        return PagingReportInfo.builder()
                .reportInfoList(reportInfoList)
                .totalPages(totalPages)
                .hasNextPage(hasNextPage)
                .hasPreviousPage(hasPreviousPage)
                .build();
    }
}
