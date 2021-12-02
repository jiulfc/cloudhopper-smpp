package com.cloudhopper.smpp.pdu;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2015 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.smpp.type.DestAddress;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.cloudhopper.smpp.util.ByteBufUtil;
import com.cloudhopper.smpp.util.PduUtil;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public abstract class BaseSmMultiResp extends PduResponse {

    private String messageId;
    private byte nounsuccess;
    private List<DestAddress> unsuccessList;

    public BaseSmMultiResp(int commandId, String name) {
        super(commandId, name);
    }

    public String getMessageId() {
        return this.messageId;
    }

    public byte getNounsuccess() {
        return this.nounsuccess;
    }

    public void setNounsuccess(byte nounsuccess) {
        this.nounsuccess = nounsuccess;
    }

    public List<DestAddress> getUnsuccessList() {
        return unsuccessList;
    }

    public void setUnsuccessList(List<DestAddress> unsuccessList) {
        this.unsuccessList = unsuccessList;
    }

    public void setMessageId(String value) {
        this.messageId = value;
    }

    @Override
    public void readBody(ByteBuf buffer) throws UnrecoverablePduException, RecoverablePduException {
        this.messageId = ByteBufUtil.readNullTerminatedString(buffer);
        this.nounsuccess = buffer.readByte();
        this.unsuccessList = ByteBufUtil.readDestAddressList(this.nounsuccess, buffer);
    }

    @Override
    public int calculateByteSizeOfBody() {
        int bodyLength = 0;
        bodyLength += PduUtil.calculateByteSizeOfNullTerminatedString(this.messageId);
        bodyLength += 1;
        bodyLength += PduUtil.calculateByteSizeOfDestAddressList(this.unsuccessList);
        return bodyLength;
    }

    @Override
    public void writeBody(ByteBuf buffer) throws UnrecoverablePduException, RecoverablePduException {
        if (!((buffer.writableBytes() == 0) && (this.messageId == null))) {
            ByteBufUtil.writeNullTerminatedString(buffer, this.messageId);
            buffer.writeByte(this.nounsuccess);
            ByteBufUtil.writeDestAddressList(buffer, this.unsuccessList);
        }
    }

    @Override
    public void appendBodyToString(StringBuilder buffer) {
        buffer.append("(messageId [");
        buffer.append(StringUtil.toStringWithNullAsEmpty(this.messageId));
        buffer.append("] destAddrNumber [0x");
        buffer.append(HexUtil.toHexString(this.nounsuccess));
        buffer.append("] destAddrList [");
        buffer.append(StringUtil.toStringWithNullAsEmpty(this.unsuccessList));
        buffer.append("])");
    }
}