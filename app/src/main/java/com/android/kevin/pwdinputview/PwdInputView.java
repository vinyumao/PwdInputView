package com.android.kevin.pwdinputview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * 描述: 仿支付宝密码框
 * --------------------------------------------
 * 工程:
 * #0000     mwy     创建日期: 2016-07-26 11:05
 */
public class PwdInputView extends EditText {
	private int length;//最大输入长度
	private int borderColor; //边框颜色
	private int dotColor;   //掩盖点颜色
	private int roundRadius; //圆角程度

	private Paint borderPaint; //边框画笔
	private Paint dotPaint; //掩盖点画笔
	private RectF roundRect; //圆矩形

	private InputMethodManager input; //输入法管理器

	private ArrayList<Integer> pwdList; //输入结果保存的集合
	private String pwd;

	private int size; //每一隔默认大小

	private InputCallBack callBack;

	public interface InputCallBack {
		void onInputFinished(String result);
	}


	public PwdInputView(Context context) {
		this(context, null);
	}

	public PwdInputView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PwdInputView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	//初始化相关参数
	private void init(AttributeSet attrs) {
		input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		pwdList = new ArrayList<>();
		pwd = "";
		borderColor = 0xffc2c2c2;
		dotColor = 0xff6b6b6b;
		roundRadius = (int) (5 * getResources().getDisplayMetrics().density);
		length = 6;

		size = (int) (30 * getResources().getDisplayMetrics().density);

		TypedArray ta = null;
		if (null != attrs) {
			ta = getContext().obtainStyledAttributes(attrs, R.styleable.PwdInputView);
			length = ta.getInt(R.styleable.PwdInputView_length, length);
			borderColor = ta.getColor(R.styleable.PwdInputView_borderColor, borderColor);
			dotColor = ta.getColor(R.styleable.PwdInputView_dotColor, dotColor);
		}
		if (null != ta) {
			ta.recycle();
		}

		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);

		dotPaint = new Paint();
		dotPaint.setAntiAlias(true);
		dotPaint.setColor(dotColor);

		roundRect = new RectF();

		//设置最大输入长度
		this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
		//去掉光标
		this.setCursorVisible(false);
		//隐藏输入字符
		this.setTextColor(Color.TRANSPARENT);

		//onKeyListener 只能监听硬键盘和模拟器的软键盘  不能监听软键盘 只能监听到软键盘的 退格 回车 空格
		//this.setOnKeyListener(new MyOnKeyListener());
		this.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				pwd = s.toString();
				invalidate();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		//因为 要点击弹出数字键盘 所以继承了TextView
		//设置弹出 数字键盘
		setInputType(InputType.TYPE_CLASS_NUMBER);
		//设置字体颜色为透明 然输入字体看不见
		//setTextColor(Color.TRANSPARENT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = measureWidth(widthMeasureSpec);
		int h = measureHeight(heightMeasureSpec);
		int wsize = MeasureSpec.getSize(widthMeasureSpec);
		int hsize = MeasureSpec.getSize(heightMeasureSpec);

		if (w == -1) {//如果宽度 不确定的时候
			if (h != -1) {//宽度确定时
				w = size * length;
				size = h;
			} else {//两个都不确定时
				w = size * length;
				h = size;
			}
		} else {//高度确定时
			if (h == -1) {//高度不确定时
				h = w / length;
				size = h;
			}
		}
		setMeasuredDimension(Math.min(w, wsize), Math.min(h, hsize));
	}

	private int measureWidth(int widthMeasureSpec) {
		int wmode = MeasureSpec.getMode(widthMeasureSpec);
		int wsize = MeasureSpec.getSize(widthMeasureSpec);
		if (wmode == MeasureSpec.AT_MOST) {//wrap_content
			return -1;
		}
		return wsize;
	}

	private int measureHeight(int heightMeasureSpec) {
		int hmode = MeasureSpec.getMode(heightMeasureSpec);
		int hsize = MeasureSpec.getSize(heightMeasureSpec);
		if (hmode == MeasureSpec.AT_MOST) {
			return -1;
		}
		return hsize;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth() - 2;
		int height = getHeight() - 2;
		//画圆角矩形
		roundRect.set(0, 0, width, height);
		canvas.drawRoundRect(roundRect, roundRadius, roundRadius, borderPaint);
		//画分割线
		for (int i = 1; i < length; i++) {
			int x = size * i;
			canvas.drawLine(x, 0, x, height, borderPaint);
		}
		//画掩盖点
		int dotRadius = size / 6;//掩盖点的半径  掩盖点占格子的三分之一
		for (int i = 0; i < pwd.length(); i++) {
			float x = size * (i + 0.5f);
			float y = size * 0.5f;
			canvas.drawCircle(x, y, dotRadius, dotPaint);
		}
	}

	private class MyOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.i("MyOnKeyListener", "" + keyCode);
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				//只处理数字
				if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
					if (pwdList.size() <= length) {
						pwdList.add(keyCode - 7);
						invalidate();
						ensureFinishInput();
					}
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (!pwdList.isEmpty()) {
						pwdList.remove(pwdList.size() - 1);//删除集合最后一个
						invalidate();
					}
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
					ensureFinishInput();
					return true;
				}
			}
			return false;
		}

		/**
		 * 判断是否输入完成，输入完成后调用callback
		 */
		void ensureFinishInput() {
			if (pwdList.size() == length && callBack != null) {
				StringBuffer sb = new StringBuffer();
				for (int i : pwdList) {
					sb.append(i);
				}
				callBack.onInputFinished(sb.toString());
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//点击弹出键盘
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			requestFocus();
			input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
			return true;
		}
		return super.onTouchEvent(event);
	}


	/*@Override
	public boolean onCheckIsTextEditor() {
		//重写这个方法返回true，是为了告诉系统，我这个view可以接受输入
		return true;
	}*/

	public void setInputCallBack(InputCallBack callBack) {
		this.callBack = callBack;
	}


	/*@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//输入类型为数字
		outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
		return new MyInputConnection(this, false);

	}*/

	class MyInputConnection extends BaseInputConnection {

		public MyInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		/**
		 * 这个方法是接受输入法的文本的，我们只处理数字，所以不用做任何操作
		 *
		 * @param text
		 * @param newCursorPosition
		 * @return
		 */
		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			return true;
		}

		/**
		 * 软件盘的删除键 DEL 这个键是无法监听的，要自己发送del事件
		 *
		 * @param beforeLength
		 * @param afterLength
		 * @return
		 */
		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			if (beforeLength == 1 && afterLength == 0) {
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}
			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {
			input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
		} else {
			input.hideSoftInputFromInputMethod(this.getWindowToken(), 0);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (!hasWindowFocus) {
			input.hideSoftInputFromWindow(this.getWindowToken(), 0);
		}
	}

}
