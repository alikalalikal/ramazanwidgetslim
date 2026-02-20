package com.konya.ramazanwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import java.util.*

class RamazanWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
    super.onEnabled(context)
    scheduleUpdate(context)
}

override fun onAppWidgetOptionsChanged(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    newOptions: android.os.Bundle
) {
    updateWidget(context, appWidgetManager, appWidgetId)
}

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateWidget(context, appWidgetManager, id)
        scheduleUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.konya.ramazanwidget.UPDATE") {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, RamazanWidget::class.java))
            for (id in ids) updateWidget(context, mgr, id)
            scheduleUpdate(context)
        }
    }

    companion object {
        data class Gun(val tarih: String, val hicri: String, val gun: String,
            val imsak: String, val ogle: String, val ikindi: String,
            val aksam: String, val yatsi: String, val kadir: Boolean = false)

        val DATA = listOf(
            Gun("19 Şubat 2026","1 Ramazan 1447","Perşembe","06:08","13:09","16:11","18:39","19:55"),
            Gun("20 Şubat 2026","2 Ramazan 1447","Cuma","06:06","13:09","16:11","18:40","19:56"),
            Gun("21 Şubat 2026","3 Ramazan 1447","Cumartesi","06:05","13:09","16:12","18:41","19:57"),
            Gun("22 Şubat 2026","4 Ramazan 1447","Pazar","06:04","13:09","16:13","18:43","19:58"),
            Gun("23 Şubat 2026","5 Ramazan 1447","Pazartesi","06:03","13:08","16:14","18:44","19:59"),
            Gun("24 Şubat 2026","6 Ramazan 1447","Salı","06:02","13:08","16:14","18:45","20:00"),
            Gun("25 Şubat 2026","7 Ramazan 1447","Çarşamba","06:00","13:08","16:15","18:46","20:01"),
            Gun("26 Şubat 2026","8 Ramazan 1447","Perşembe","05:59","13:08","16:16","18:47","20:02"),
            Gun("27 Şubat 2026","9 Ramazan 1447","Cuma","05:58","13:08","16:16","18:48","20:03"),
            Gun("28 Şubat 2026","10 Ramazan 1447","Cumartesi","05:56","13:08","16:17","18:49","20:04"),
            Gun("1 Mart 2026","11 Ramazan 1447","Pazar","05:55","13:07","16:18","18:50","20:05"),
            Gun("2 Mart 2026","12 Ramazan 1447","Pazartesi","05:54","13:07","16:18","18:51","20:06"),
            Gun("3 Mart 2026","13 Ramazan 1447","Salı","05:52","13:07","16:19","18:52","20:07"),
            Gun("4 Mart 2026","14 Ramazan 1447","Çarşamba","05:51","13:07","16:19","18:53","20:08"),
            Gun("5 Mart 2026","15 Ramazan 1447","Perşembe","05:49","13:07","16:20","18:54","20:09"),
            Gun("6 Mart 2026","16 Ramazan 1447","Cuma","05:48","13:06","16:21","18:55","20:10"),
            Gun("7 Mart 2026","17 Ramazan 1447","Cumartesi","05:46","13:06","16:21","18:56","20:11"),
            Gun("8 Mart 2026","18 Ramazan 1447","Pazar","05:45","13:06","16:22","18:57","20:12"),
            Gun("9 Mart 2026","19 Ramazan 1447","Pazartesi","05:44","13:06","16:22","18:58","20:13"),
            Gun("10 Mart 2026","20 Ramazan 1447","Salı","05:42","13:05","16:23","18:59","20:14"),
            Gun("11 Mart 2026","21 Ramazan 1447","Çarşamba","05:40","13:05","16:23","19:00","20:15"),
            Gun("12 Mart 2026","22 Ramazan 1447","Perşembe","05:39","13:05","16:24","19:01","20:16"),
            Gun("13 Mart 2026","23 Ramazan 1447","Cuma","05:37","13:05","16:24","19:02","20:17"),
            Gun("14 Mart 2026","24 Ramazan 1447","Cumartesi","05:36","13:04","16:25","19:02","20:18"),
            Gun("15 Mart 2026","25 Ramazan 1447","Pazar","05:34","13:04","16:25","19:03","20:19"),
            Gun("16 Mart 2026","26 Ramazan 1447","Pazartesi","05:33","13:04","16:26","19:04","20:20"),
            Gun("17 Mart 2026","27 Ramazan 1447","Salı","05:31","13:04","16:26","19:05","20:21",true),
            Gun("18 Mart 2026","28 Ramazan 1447","Çarşamba","05:30","13:03","16:27","19:06","20:22"),
            Gun("19 Mart 2026","29 Ramazan 1447","Perşembe","05:28","13:03","16:27","19:07","20:23")
        )

        val MONTHS = mapOf("Ocak" to 0,"Şubat" to 1,"Mart" to 2,"Nisan" to 3,
            "Mayıs" to 4,"Haziran" to 5,"Temmuz" to 6,"Ağustos" to 7,
            "Eylül" to 8,"Ekim" to 9,"Kasım" to 10,"Aralık" to 11)

        fun bugun(): Pair<Gun?, Int> {
            val cal = Calendar.getInstance()
            DATA.forEachIndexed { i, g ->
                val p = g.tarih.split(" ")
                if (p[0].toInt() == cal.get(Calendar.DAY_OF_MONTH) &&
                    MONTHS[p[1]] == cal.get(Calendar.MONTH) &&
                    p[2].toInt() == cal.get(Calendar.YEAR))
                    return Pair(g, i + 1)
            }
            return Pair(null, 0)
        }

        fun saatCal(saat: String, ertesi: Boolean = false): Calendar {
            val p = saat.split(":")
            return Calendar.getInstance().apply {
                if (ertesi) add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, p[0].toInt())
                set(Calendar.MINUTE, p[1].toInt())
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
        }

        fun kalanGun(): Int {
            val now = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY,0);set(Calendar.MINUTE,0);set(Calendar.SECOND,0) }
            val bayram = Calendar.getInstance().apply { set(2026,2,20,0,0,0) }
            val d = ((bayram.timeInMillis - now.timeInMillis) / 86400000).toInt()
            return if (d > 0) d else 0
        }

        fun updateWidget(context: Context, mgr: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val (bugun, gunNo) = bugun()

            if (bugun == null) {
                views.setTextViewText(R.id.tv_iftar, "Ramazan dışı")
                views.setTextViewText(R.id.tv_countdown, "--:--:--")
                views.setTextViewText(R.id.tv_gun, "")
                views.setTextViewText(R.id.tv_hicri, "")
                views.setTextViewText(R.id.tv_gun_no, "")
                views.setTextViewText(R.id.tv_kalan, "")
                views.setTextViewText(R.id.tv_imsak_val, "--:--")
                views.setTextViewText(R.id.tv_ogle_val, "--:--")
                views.setTextViewText(R.id.tv_ikindi_val, "--:--")
                views.setTextViewText(R.id.tv_aksam_val, "--:--")
                views.setTextViewText(R.id.tv_yatsi_val, "--:--")
                mgr.updateAppWidget(widgetId, views)
                return
            }

            val now = Calendar.getInstance()
            val imsak = saatCal(bugun.imsak)
            val aksam = saatCal(bugun.aksam)
            val imsakErtesi = saatCal(bugun.imsak, true)
            val isIftar = now.after(imsak) && now.before(aksam)
            val hedef = if (isIftar) aksam else if (now.before(imsak)) imsak else imsakErtesi
            val diff = hedef.timeInMillis - now.timeInMillis
            val hh = (diff / 3600000).toInt()
            val mm = ((diff % 3600000) / 60000).toInt()
            val ss = ((diff % 60000) / 1000).toInt()

            views.setTextViewText(R.id.tv_hicri, bugun.hicri)
            views.setTextViewText(R.id.tv_gun, "${bugun.tarih}  •  ${bugun.gun}")
            views.setTextViewText(R.id.tv_countdown_label, if (isIftar) "İftara Kalan" else "İmsaka Kalan")
            views.setTextViewText(R.id.tv_countdown, "%02d:%02d:%02d".format(hh, mm, ss))
            views.setTextViewText(R.id.tv_gun_no, "$gunNo. Gün")
            views.setTextViewText(R.id.tv_kalan, "${kalanGun()} Gün Kaldı")
            views.setTextViewText(R.id.tv_iftar, "İftar: ${bugun.aksam}")
            views.setTextViewText(R.id.tv_imsak_val, bugun.imsak)
            views.setTextViewText(R.id.tv_ogle_val, bugun.ogle)
            views.setTextViewText(R.id.tv_ikindi_val, bugun.ikindi)
            views.setTextViewText(R.id.tv_aksam_val, bugun.aksam)
            views.setTextViewText(R.id.tv_yatsi_val, bugun.yatsi)
            views.setViewVisibility(R.id.tv_kadir, if (bugun.kadir) android.view.View.VISIBLE else android.view.View.GONE)

            mgr.updateAppWidget(widgetId, views)
        }

        fun scheduleUpdate(context: Context) {
            val intent = Intent(context, RamazanWidget::class.java).apply { action = "com.konya.ramazanwidget.UPDATE" }
            val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, pi)
        }
    }
}
