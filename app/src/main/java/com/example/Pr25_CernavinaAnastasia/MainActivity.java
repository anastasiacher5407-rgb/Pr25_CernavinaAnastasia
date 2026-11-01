package com.example.a25pr;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory; // Импорт для картинки
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    // Идентификатор канала уведомлений
    private static final String CHANNEL_ID = "cat_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаем канал уведомлений
        createNotificationChannel();

        // Запрашиваем разрешение у пользователя (для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    // Метод создания канала
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Канал для котов",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // Этот метод вызывается при нажатии на кнопку на экране
    @SuppressLint("MissingPermission")
    public void showNotification(View view) {
        // 1. Намерение для клика по уведомлению
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 2. Готовим текст, который будет под картинкой
        String bigText = "Это я, почтальон Печкин. Принёс для вас посылку. " +
                "Только я вам её не отдам. Потому что у вас документов нету. " +
                "И вообще, ваш кот уже заждался ужина!";

        // 3. Строим уведомление с кнопками и стилями
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Посылка")
                .setContentText("Это я, почтальон Печкин. Принёс для вас посылку")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

                // --- КНОПКИ ---
                .addAction(android.R.drawable.ic_menu_view, "Открыть", pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Отказаться", pendingIntent)

                // --- ОГРОМНЫЙ КОТ (BigPictureStyle) И ТЕКСТ ПОД НИМ ---
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.hungrycat))
                        .setSummaryText(bigText))

                // --- ЗВУК И ВИБРАЦИЯ ---
                .setDefaults(android.app.Notification.DEFAULT_ALL)

                // Автоматическое закрытие после нажатия
                .setAutoCancel(true);

        // 4. Отправляем
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(101, builder.build());
        } else {
            Toast.makeText(this, "Нет разрешения на уведомления", Toast.LENGTH_SHORT).show();
        }
    }
}