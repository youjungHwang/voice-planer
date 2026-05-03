package com.voiceplaner.app.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.voiceplaner.app.R
import java.text.NumberFormat
import java.util.Locale

class LedgerWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it, 0L) }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, id: Int, expense: Long) {
            val views = RemoteViews(context.packageName, R.layout.widget_ledger)
            val formatted = NumberFormat.getNumberInstance(Locale.KOREA).format(expense) + "원"
            views.setTextViewText(R.id.tv_widget_expense, "이번 달 지출\n$formatted")
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
