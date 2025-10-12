package com.slateblua.meent.data.mappers

import com.slateblua.meent.data.db.FocusEntry
import com.slateblua.meent.data.FocusModel

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