package com.example.eticaretapp.helpers;

import android.animation.LayoutTransition;
import android.view.View;
import android.widget.LinearLayout;

public class AnimationHelper {

    public static void showViewWithAnimation(final View view) {
        // İlk olarak, görünümü görünür yapmadan önce görünürlüğünü INVISIBLE olarak ayarlayın
        view.setVisibility(View.INVISIBLE);

        // Görünümün görünür hale gelmesi için animasyonu başlatın
        view.animate()
                .alpha(1f)
                .setDuration(500)  // Animasyon süresi
                .withStartAction(() -> view.setVisibility(View.VISIBLE))  // Animasyon başlamadan önce görünürlüğü VISIBLE yap
                .start();
    }

    public static void hideViewWithAnimation(final View view) {
        view.animate().alpha(0f)
                .setDuration(500)  // Animasyon süresi
                .withEndAction(() -> {
                            view.setVisibility(View.GONE);
                            view.setAlpha(1f);
                        }  // Animasyon bittiğinde görünürlüğü kapat
                ).start();
    }

    public static void setupLayoutTransition(LinearLayout layout) {
        LayoutTransition transition = new LayoutTransition();

        // Animasyon süresini artırarak daha yumuşak geçişler sağlayabilirsiniz
        transition.setDuration(LayoutTransition.APPEARING, 200);
        transition.setDuration(LayoutTransition.DISAPPEARING, 200);
        layout.setLayoutTransition(transition);
    }
}
