package cn.mointe.vaccination.view;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyAnimation {

	// 图标的动画(入动画)
	public static void startAnimationsIn(ViewGroup viewgroup, int durationMillis) {

		viewgroup.setVisibility(0);
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			viewgroup.getChildAt(i).setVisibility(0);
			viewgroup.getChildAt(i).setClickable(true);
			viewgroup.getChildAt(i).setFocusable(true);
		}
		Animation animation;
		animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(durationMillis);
		viewgroup.startAnimation(animation);

	}

	// 图标的动画(出动画)
	public static void startAnimationsOut(final ViewGroup viewgroup,
			int durationMillis, int startOffset) {

		Animation animation;
		animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(durationMillis);
		animation.setStartOffset(startOffset);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				viewgroup.setVisibility(8);
				for (int i = 0; i < viewgroup.getChildCount(); i++) {
					viewgroup.getChildAt(i).setVisibility(8);
					viewgroup.getChildAt(i).setClickable(false);
					viewgroup.getChildAt(i).setFocusable(false);
				}
			}
		});
		viewgroup.startAnimation(animation);
	}

}
