package com.cloudhopper.smpp.type;

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.util.ByteBufUtil;
import com.cloudhopper.smpp.util.PduUtil;
import io.netty.buffer.ByteBuf;

public class DestAddress {

    private byte destFlag;
    private Address address;
    private String dlName;

    public DestAddress(byte destFlag, Address address) {
        this.destFlag = destFlag;
        this.address = address;
    }

    public DestAddress(byte destFlag, String dlName) {
        this.destFlag = destFlag;
        this.dlName = dlName;
    }

    public byte getDestFlag() {
        return destFlag;
    }

    public void setDestFlag(byte destFlag) {
        this.destFlag = destFlag;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDlName() {
        return dlName;
    }

    public void setDlName(String dlName) {
        this.dlName = dlName;
    }

    public int calculateByteSize() {
        if (destFlag == SmppConstants.SM_DEST_DL_NAME) {
            return 1 + PduUtil.calculateByteSizeOfNullTerminatedString(this.dlName);
        }
        return 1 + this.address.calculateByteSize();
    }

    public void write(ByteBuf buffer) throws UnrecoverablePduException, RecoverablePduException {
        buffer.writeByte(this.destFlag);
        if (this.destFlag == SmppConstants.SM_DEST_DL_NAME) {
            ByteBufUtil.writeNullTerminatedString(buffer, this.dlName);
        } else {
            ByteBufUtil.writeAddress(buffer, this.address);
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(40);
        buffer.append("0x");
        buffer.append(HexUtil.toHexString(this.destFlag));
        if (this.destFlag == SmppConstants.SM_DEST_DL_NAME) {
            buffer.append(" [");
            buffer.append(StringUtil.toStringWithNullAsEmpty(this.dlName));
            buffer.append("]");
        } else {
            buffer.append(" [");
            buffer.append(this.address.toString());
            buffer.append("]");
        }
        return buffer.toString();
    }
}
