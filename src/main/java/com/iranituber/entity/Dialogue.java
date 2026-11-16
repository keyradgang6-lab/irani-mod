package com.iranituber.entity;

/**
 * تعداد جمله‌های هر دسته. متن جمله‌ها توی فایل‌های lang هست:
 * dialogue.irani_tuber.<category>.<1..count>
 * اگه جمله اضافه کردی، هم این عدد رو بالا ببر هم فایل‌های fa_ir.json و en_us.json رو آپدیت کن.
 */
public final class Dialogue {
	private Dialogue() {}

	public static final int IDLE = 12;
	public static final int INTERACT = 10;
	public static final int HURT = 8;
	public static final int CAMERA_START = 8;
	public static final int CAMERA_DONE = 6;

	/** تعداد عنوان‌های ویدیو: video.irani_tuber.title.<1..count> */
	public static final int VIDEO_TITLES = 10;
}
