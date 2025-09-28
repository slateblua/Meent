package com.slateblua.meent.data.db

fun FocusEntry.toModel() = FocusModel(
    focusId = focusId,
    start = start,
    end = end,
    planned = planned,
)

fun FocusModel.toEntry() = FocusEntry(
    focusId = focusId,
    start = start,
    end = end,
    planned = planned,
)