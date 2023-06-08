package Codec;

import Codec.Encoder.StringToByteEncoder;
import Codec.Decoder.ByteToStringDecoder;
import io.netty.channel.CombinedChannelDuplexHandler;

public class CombinedByteStringCodec extends CombinedChannelDuplexHandler<ByteToStringDecoder, StringToByteEncoder> {

    //기존 encoder와 decoder를 분리하면서 결합하여 사용가능하도록 combinedChannelDuplexHandler를 사용
    public CombinedByteStringCodec() {
        super(new ByteToStringDecoder(), new StringToByteEncoder());
    }
}
