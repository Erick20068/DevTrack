package com.erickguaillas.devtrack.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.view.KeyEvent
import com.erickguaillas.devtrack.media.DevNotificationListener

/**
 * Controla la reproducción del dispositivo (incluido Spotify) vía MediaSession.
 * Requiere que el usuario active "Acceso a notificaciones" para DevTrack UNA vez.
 */
object SpotifyMedia {

    fun hasAccess(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        ) ?: return false
        return enabled.contains(context.packageName)
    }

    fun accessIntent(): Intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

    /** Devuelve true si encontró una sesión de Spotify y envió el botón */
    fun dispatch(context: Context, keyCode: Int): Boolean {
        if (!hasAccess(context)) return false
        return try {
            val mgr = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val component = ComponentName(context, DevNotificationListener::class.java)
            val sessions = mgr.getActiveSessions(component)
            val spotify = sessions.firstOrNull {
                it.packageName.contains("spotify", ignoreCase = true)
            } ?: sessions.firstOrNull() // cualquier reproductor activo

            spotify?.let {
                val down = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
                val up = KeyEvent(KeyEvent.ACTION_UP, keyCode)
                it.dispatchMediaButtonEvent(down)
                it.dispatchMediaButtonEvent(up)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun next(context: Context) = dispatch(context, KeyEvent.KEYCODE_MEDIA_NEXT)
    fun prev(context: Context) = dispatch(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    fun playPause(context: Context) = dispatch(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
}
