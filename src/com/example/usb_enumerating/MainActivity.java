package com.example.usb_enumerating;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Iterator;

import android.R.integer;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG="MainActivity";
	private ProgressDialog wait;
	private int k,k1,k2,maxsize,k3,k4,k5,live,life;
	private int[] datereceive=new int[6];
	private String[] ZigBee = {"",""};
	private UsbDevice device;
	private TextView textView;
	private TextView tv;
	private boolean USB_staus;
	private boolean _Thread;
	private boolean listen_Thread;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private byte[] buffer = new byte[2048];
	private byte[] en3 = new byte[2048];
	private File file;
	private char [] maik=new char[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		textView=(TextView)findViewById(R.id.textView1);
		tv=(TextView)findViewById(R.id.textView2);
		 textView.setText("");
		 textView.setScrollContainer(true);
		tv.setText("");
		 tv.setScrollContainer(true);
		 filter.addAction(Intent.ACTION_POWER_CONNECTED);

			filter.addAction("android.intent.action.MEDIA_MOUNTED");
			filter.addAction("android.intent.action.MEDIA_EJECT");

			filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
			filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
			filter.addAction(UsbManager.EXTRA_DEVICE);
			filter.addAction(UsbManager.EXTRA_ACCESSORY);

			filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
			filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
			Button button1=(Button)findViewById(R.id.button1);
			button1.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					wait=ProgressDialog.show(MainActivity.this, "", "正在接收");
					listen_Thread=true;
					new Thread(open_device).start();
				}
				
			});
			Button button2=(Button)findViewById(R.id.button2);
			button2.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					wait=ProgressDialog.show(MainActivity.this, "", "正在接收");
					new Thread(Device_listen).start();
				}
				
			});
	}
	public void onResume(){
		this.registerReceiver(receiver, filter);
		this.registerReceiver(mReceiver, filter);
		//new Thread(Device_listen).start();
		super.onResume();
	}
	public void onStop(){
		this.unregisterReceiver(receiver);
		this.unregisterReceiver(mReceiver);
		super.onStop();
	}
	public void onDestroy(){
		listen_Thread=true;
		_Thread=true;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			textView.setText(textView.getText() + "  onReceive = " + action + "\n");

			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);

					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							Log.d(TAG,
									"permission for device-------------------------- ");
						}
					} else {
						Log.d(TAG, "permission denied for device " + device);
					}
				}
			}
			
		}
	};
	private IntentFilter filter=new IntentFilter();
	private BroadcastReceiver mReceiver=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action=intent.getAction();			
				Log.d(TAG, "接受意图"+action);
				textView.setText(textView.getText()+"接受意图"+action);
				UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)||action.equals(Intent.ACTION_UMS_CONNECTED)) {
					Toast.makeText(context, "USB已连接", 3000).show();
					textView.setText(textView.getText()+"USB成功连接");
					USB_staus=true;
					
				}
				if (device!=null) {
					textView.setText(textView.getText()+"USB成功连接");
				}
				if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
					Toast.makeText(context, "USB设备断开", 3000).show();
					//USB_staus=true;
				}
				/*if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					Toast.makeText(context, "USB已连接", 3000).show();
					USB_staus=true;
					
				} else {
					textView.setText(textView.getText()+"USB未能成功连接");
				}*/
			}
		};
		private String makeJoystickIdentify(int vendorId, int productId) {
			String strVendorId = Integer.toHexString(vendorId);
			String strProductId = Integer.toHexString(productId);
			int vendorIdLength = strVendorId.length();
			int productIdLength = strProductId.length();
			for (int i = 4 - vendorIdLength; i > 0; i--) {
				strVendorId = "0" + strVendorId;
			}
			for (int i = 4 - productIdLength; i > 0; i--) {
				strProductId = "0" + strProductId;
			}

			return strVendorId + ":" + strProductId;
		}
		public void request(UsbDevice mdevice){
			UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			PendingIntent pendingIntent=PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
			mUsbManager.requestPermission(mdevice, pendingIntent);
		}
		Runnable Device_listen=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "线程1启动");
				UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
				String USB=null;
				while (!listen_Thread) {
					Log.i(TAG, "线程1计时");
					HashMap<String, UsbDevice> deviceList=manager.getDeviceList();
					Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
					if (ZigBee[0].equals("")||ZigBee[1].equals("")) {
						while (deviceIterator.hasNext()) {
							String strdevice="";
							UsbDevice device = (UsbDevice) deviceIterator.next();
							 UsbInterface intf = device.getInterface(0);
							 UsbEndpoint endpoint=intf.getEndpoint(0);
							 UsbEndpoint endpoint1=intf.getEndpoint(1);
							 request(device);
							 UsbDeviceConnection connection=manager.openDevice(device);
							
								 //tv.setText(tv.getText()+"打开成功");
								//Toast.makeText(MainActivity.this, "打开成功", 3000).show();
							
							int vendorId = device.getVendorId();
							int productId = device.getProductId();
							USB = makeJoystickIdentify(vendorId, productId);
							if (!ZigBee[0].equals(USB) && !ZigBee[1].equals(USB)) {
								if (ZigBee[0].equals("")) {
									ZigBee[0]=USB;
								} else {
									ZigBee[1]=USB;
								}
								strdevice=strdevice
										//+ "\n"+"USB接口版本ID:" + ZigBee[0]
										//+ "\n"+"USB接口生产ID:" + ZigBee[1]
										+"\n"+"设备名称:"+device.getDeviceName()
										//+"\n"+"类       别:"+device.getDeviceClass()
										//+"\n"+"设备ID:"+device.getDeviceId()
										+"\n"+"总体信息: "+device.toString()
										//+"\n"+"接口数量:"+device.getInterfaceCount()
										//+"\n"+"端点数量:"+intf.getEndpointCount()
										+"\n"+"打开与否:"+connection
										+"\n"+"0端点类型:"+endpoint.getDirection()
										+"\n"+"1端点类型:"+endpoint1.getDirection()
										//+"\n"+"声明成功:"+connection.claimInterface(intf, true)
										+"\n"+"类型:"+device.hashCode();
								Message msg1=new Message();
								msg1.what=0;
								msg1.obj=strdevice;
								myhaHandler.sendMessage(msg1);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						finally{
							wait.dismiss();
						}
					}
				}
			}
		};
		Runnable open_device=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d(TAG, "线程2启动");
				UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
				String USB=null;
				String temp=null;
				String anay=null;
				String mString=null;
				/*if (listen_Thread) {
					Log.i(TAG, "线程1已经停止");
				}*/
				while (!_Thread) {
					//Log.i(TAG, "已经开始");
					HashMap<String, UsbDevice> deviceList=manager.getDeviceList();
					Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
					if (ZigBee[0].equals("")||ZigBee[1].equals("")) {
						while (deviceIterator.hasNext()) {
							String strdevice="";
							//String zigdevice="";
							UsbDevice device = (UsbDevice) deviceIterator.next();
							 UsbInterface intf = device.getInterface(0);
							 UsbEndpoint endpoint=intf.getEndpoint(0);
							 UsbEndpoint endpoint1=intf.getEndpoint(1);
							
							//&&intf.getEndpointCount()==3
							int vendorId = device.getVendorId();
							int productId = device.getProductId();
							USB = makeJoystickIdentify(vendorId, productId);
							if (!ZigBee[0].equals(USB) && !ZigBee[1].equals(USB)&&intf.getEndpointCount()==3) {
								if (ZigBee[0].equals("")) {
									ZigBee[0]=USB;
								} else {
									ZigBee[1]=USB;
								}
								UsbEndpoint endpoint2=intf.getEndpoint(2);
								request(device);
								 UsbDeviceConnection connection=manager.openDevice(device);
								connection.claimInterface(intf, true);
								UsbRequest request=new UsbRequest();
								//request.initialize(connection, endpoint);
								//request.initialize(connection, endpoint1);
								//boolean str=request.initialize(connection, endpoint2);
								request.initialize(connection, endpoint2);
								maxsize=endpoint2.getMaxPacketSize();
								char my[]=new char[24];
								my[0]='0';
								my[1]='x';
								my[2]='e';
								my[3]='f';
								my[4]='0';
								my[5]='x';
								my[6]='C';
								my[7]='0';
								my[8]='0';
								my[9]='x';
								my[10]='0';
								my[11]='0';
								my[12]='0';
								my[13]='x';
								my[14]='0';
								my[15]='0';
								my[16]='0';
								my[17]='x';
								my[18]='C';
								my[19]='0';
								my[20]='0';
								my[21]='x';
								my[22]='f';
								my[23]='e';
								
								
								/*my[1]=0xC0;
								my[2]=0x00;
								my[3]=0x00;
								my[4]=0xC0;
								my[5]=0xfe;*/
								
								
								
								
								int[] mm=new int[6];
								mm[0]= 0xef;
								mm[1]= 0xC5;
								mm[2]=0x00;
								mm[3]=0x00;
								mm[4]=0xC5;
								mm[5]= 0xfe;
								byte[] send0=int2byte(mm[0]);
								byte[] send1=int2byte(mm[1]);
								byte[] send2=int2byte(mm[2]);
								byte[] send3=int2byte(mm[3]);
								byte[] send4=int2byte(mm[4]);
								byte[] send5=int2byte(mm[5]);
								byte[] se=new byte[24];
								for (int i = 0; i < se.length; i++) {
									if (i<4) {
										se[i]=send0[i];	
									}
									if (i>=4&&i<8) {
										se[i]=send1[i-4];
									}
									if (i>=8&&i<12) {
										se[i]=send2[i-8];
									}
									if (i>=12&&i<16) {
										se[i]=send3[i-12];
									}
									if (i>=16&&i<20) {
										se[i]=send4[i-16];
									}
									if (i>=20&&i<24) {
										se[i]=send5[i-20];
									}
								}
								
								
								
								
								byte[] by=getBytes(my);
								live=byte2Int(se);
								ByteBuffer buffer1 = ByteBuffer.allocate(maxsize);
								try {
									request.queue(buffer1, maxsize);
									//connection.bulkTransfer(endpoint1, by, by.length, 0);
									//connection.bulkTransfer(endpoint1, by, by.length, 0);
									//connection.bulkTransfer(endpoint1, by, by.length, 0);
									//k1=connection.controlTransfer(0x40, 0x1A1, 0, 0, by, by.length, 0);
								//	k2=connection.controlTransfer(0x40, 0x1A4, 0, 0, by, by.length, 0);//可行
									//connection.controlTransfer(0x40, 0x19A, 0, 0, by, by.length, 0);
									//connection.controlTransfer(0x40, 0x19A, 0, 0, by, by.length, 0);
									//k3=connection.controlTransfer(0x40, 0xA1, 0, 0, by, by.length, 0);
								//	k4=connection.controlTransfer(0x40, 0xA4, 0, 0x24, by, by.length, 0);
									connection.controlTransfer(0x40, 0xA4, 0, 0, null, 0, 0);//可行
									//k5=connection.controlTransfer(0x21, 0x03, 0x4138,0 , null, 0, 0);
									//connection.bulkTransfer(endpoint1, se, se.length, 0);
									//request.getClientData();
									Log.i(TAG, "传输是否成功"+k1);
									//k1=connection.bulkTransfer(endpoint2, by, by.length,0);
									Thread.sleep(100);
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							//connection.bulkTransfer(endpoint1, by, by.length, 0);
								Log.d(TAG, "endpoint =" + endpoint + "  buffer =" + buffer );
								//wait =ProgressDialog.show(MainActivity.this, "", "数据接收中");
								//if (k1!=-1) {
									try {	
										//k=connection.controlTransfer(0x40, 0xA4, 0, 0, buffer, buffer.length, 0);
									 k=connection.bulkTransfer(endpoint, buffer, buffer.length, 0);
										//k2= connection.bulkTransfer(endpoint2, en3, en3.length, 100);
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Toast.makeText(MainActivity.this, "传输异常", 3000).show();
									}
								byte[] newdate=Base64.decode(buffer, Base64.DEFAULT);
								//USB =new String(buffer);
								USB =new String(newdate);
								try {
									anay=new String(buffer, "GB2312");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								//temp =new String(by);
								temp=by2str(anay);
								anay=new String(by);
								int temh,teml;
								int num;
								temh=buffer[4];
								teml= buffer[3];
								num=teml*625;
								char[] temp0=getChars(buffer);
								int receive=byteToInt2(buffer);
								life=byte2Int(buffer);
								datereceive[0]=0xef;
								int km=buffer[0]&0x00ff;
								int kn=buffer[1]&0x00ff;
								datereceive[1]=km;
								datereceive[2]=kn;
								datereceive[3]=buffer[2]&0x00ff;
								datereceive[4]=buffer[3]&0x00ff;
								datereceive[5]=buffer[4]&0x00ff;
								/*maik[0]='+';
								maik[1]=(char) (temh/100+0x30);
								maik[2]=(char) (temh/10%10+0x30);
								maik[3]=(char) (temh%10+0x30);
								maik[4]='.';
								maik[5]=(char) (num/1000+0x30);*/
								strdevice=strdevice
										//+ "\n"+"温度:" +maik[1]+maik[2]+maik[3]+maik[4]+maik[5]
										//+ "\n"+"设备子类:" + device.getDeviceSubclass()
										//+"\n"+"设备类别:"+device.getDeviceClass()
									//	+"\n"+"发送的数据:"+buffer[0]
										+"\n"+"输出内容:"+USB
									//	+"\n"+"输出内容:"+temp
									//	+"\n"+"输出温度:"+life
										//+"\n"+"总体信息: "+device.toString()
										//+"\n"+"端点0信息:"+endpoint1.toString()
										//+"\n"+"端点数量:"+intf.g
										//+"\n"+"发送是否成功:"+k1
										//+"\n"+"发送是否成功:"+k2
										//+"\n"+"发送是否成功:"+k3
										//+"\n"+"发送是否成功:"+k4
										//+"\n"+"发送是否成功:"+k5
										//+"\n"+"0传输是否成功:"+k3
									//	+"\n"+"接收是否成功:"+k
									//	+"\n"+"接收0:"+km
									//	+"\n"+"接收1:"+kn
									//	+"\n"+"接收2:"+datereceive[3]
									//	+"\n"+"接收3:"+datereceive[4]
									//	+"\n"+"接收4:"+datereceive[5]
										//+"\n"+"3端点类型:"+endpoint2.getType()
										//+"\n"+"3端点方向:"+endpoint2.getDirection()
									//	+"\n"+"字节换为整数:"+receive
										//+"\n"+"字节数据:"+by.length
										+"\n"+"字节数据:"+mm[1];
								Message msg=new Message();
								msg.what=0;
								msg.obj=strdevice;
								myhaHandler.sendMessage(msg);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						finally{
							
							wait.dismiss();
						}
					}
				}
			}
		};
		private Handler myhaHandler=new Handler(){
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case 0:
					Log.i(TAG, "执行到1");
					textView.setText(textView.getText()+msg.obj.toString());
					break;
				case 1:
					Log.i(TAG, "执行到2");
					tv.setText(tv.getText()+msg.obj.toString());
					break;
				}
				super.handleMessage(msg);
				}
		};
		/*private Handler my_Handler=new Handler(){
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case 1:
					Log.i(TAG, "执行到2");
					tv.setText(tv.getText()+msg.obj.toString());
					break;
				}
				super.handleMessage(msg);
				}
		};*/
		private char[] getChars (byte[] bytes) {
		      Charset cs = Charset.forName ("UTF-8");
		      ByteBuffer bb = ByteBuffer.allocate (bytes.length);
		      bb.put (bytes);
		                 bb.flip ();
		       CharBuffer cb = cs.decode (bb);
		  
		   return cb.array();
		}
		protected int byte2Int(byte[] b) {
			// TODO Auto-generated method stub
			int intValue = 0;
//	        int tempValue = 0xFF;
	        for (int i = 0; i < b.length; i++) {
	            intValue += (b[i] & 0xFF) << (8 * (3 - i));
	            // System.out.print(Integer.toBinaryString(intValue)+" ");
	        }
	        return intValue;
		}
		protected byte[] int2byte(int res) {
			// TODO Auto-generated method stub
			byte[] targets = new byte[4];

			targets[0] = (byte) (res & 0xff);// 最低位 
			targets[1] = (byte) ((res >> 8) & 0xff);// 次低位 
			targets[2] = (byte) ((res >> 16) & 0xff);// 次高位 
			targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。 
			return targets; 
		}
		private byte[] getBytes (char[] chars) {
			   Charset cs = Charset.forName ("UTF-8");
			   CharBuffer cb = CharBuffer.allocate (chars.length);
			   cb.put (chars);
			                 cb.flip ();
			   ByteBuffer bb = cs.encode (cb);
			  
			   return bb.array();
}
		private static String by2str(String str){
			if(str==null||str.length()==0){
				return "";
			}
			try {
				return new String(str.getBytes("ISO-8859-1"),"bgk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return str;
			}
		}
		public static int byteToInt2(byte[] b){  
		      return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];  
		  }  

		
}