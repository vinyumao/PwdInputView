#仿支付宝密码输入控件

###实现思路：
	-自定义一个view,画出圆角边框，以及间隔线，根据字符长度画原点
###心得：
- 由于这是一个自定义view实现的，输入密码是靠自带输入法，普通自定义控件，设置相关属性等
- 可以点击控件弹出输入法的，但是为其设置onkeylistener 来监控用户输入时，发现一个问题： onkeylistener是监控硬键盘的 也就是外接键盘的 ，在监控软键盘时 只能监控到 退格键 删除键 和空格键 其他普通数字键 字母键 字符键是监听不到的
- 但是由于不能给普通控件添加addTextChangeListener 监听，所以本控件后来直接继承了EditText 为其添加TextWatcher 在输入时画圆点,并且隐藏了EditText的光标,和设置了字体颜色为透明
###效果图

![img](https://github.com/vinyumao/PwdInputView/blob/master/app/src/main/assets/pwdInputView.gif)
