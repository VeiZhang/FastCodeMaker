package com.excellence.fastcode.maker.widget;

import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/5/16
 *     desc   :
 * </pre>
 */

public class Progress
{
	private static Progress mInstance = new Progress();

	private Stage mStage = null;

	public static Progress getInstance(Stage parent)
	{
		if (mInstance.mStage.getOwner() == null)
		{
			// 窗口父子关系
			mInstance.mStage.initOwner(parent);
		}
		return mInstance;
	}

	public Progress()
	{
		try
		{
			ProgressIndicator progressIndicator = new ProgressIndicator();
			mStage = new Stage();
			mStage.initStyle(StageStyle.UNDECORATED);
			// 去掉标题栏
			mStage.initStyle(StageStyle.TRANSPARENT);
			// 应用模态，不能操作父窗口
			mStage.initModality(Modality.APPLICATION_MODAL);
			mStage.setTitle("等待");

			Scene scene = new Scene(progressIndicator, 100, 100);
			scene.setFill(null);
			mStage.setScene(scene);
			mStage.setResizable(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void show()
	{
		if (mStage != null)
		{
			mStage.show();
		}
	}

	public void close()
	{
		if (mStage != null)
		{
			mStage.close();
		}
	}

}
