package com.collection.book.spreadsheet.domain;

import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RowData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateArea {
    private int sheetId;
    private int startRowIndex;
    private int startColumnIndex;
    private int rowSize;
    private int colSize;
    private List<RowData> rowDataList;

    public GridRange areaToGridRange() {
        return new GridRange()
                .setSheetId(sheetId)
                .setStartRowIndex(startRowIndex)
                .setStartColumnIndex(startColumnIndex)
                .setEndRowIndex(startRowIndex + rowSize)
                .setEndColumnIndex(startColumnIndex + colSize);
    }

    @Override
    public String toString() {
        return "UpdateArea{" +
                "sheetId=" + sheetId +
                ", startRowIndex=" + startRowIndex +
                ", startColumnIndex=" + startColumnIndex +
                ", rowSize=" + rowSize +
                ", colSize=" + colSize +
                ", rowDataList=" + rowDataList +
                '}';
    }
}
