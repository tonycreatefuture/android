package com.zhongdasoft.svwtrainnet.imdemo.session.action;

import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.imdemo.session.extension.MultiAttachment;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/1/10 16:18
 * 修改人：tony
 * 修改时间：2017/1/10 16:18
 * 修改备注：
 */

public class MultiAction extends BaseAction {
    public MultiAction() {
        super(R.drawable.message_plus_file_selector, R.string.input_panel_file);
    }
    @Override
    public void onClick() {
        MultiAttachment attachment = new MultiAttachment();
        IMMessage message;
        if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom) {
            message = ChatRoomMessageBuilder.createChatRoomCustomMessage(getAccount(), attachment);
        } else {
            message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment);
        }
        sendMessage(message);
    }
}
