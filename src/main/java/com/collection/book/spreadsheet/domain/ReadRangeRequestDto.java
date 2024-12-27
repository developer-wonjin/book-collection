package com.collection.book.spreadsheet.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadRangeRequestDto {
    private String sheetName;

    private String startCol;
    private String startRow;
    private String endCol;
    private String endRow;

    @Override
    public String toString() {
        return sheetName +
                "!" +
                startCol +
                startRow +
                ":" +
                endCol +
                endRow;
    }
}
