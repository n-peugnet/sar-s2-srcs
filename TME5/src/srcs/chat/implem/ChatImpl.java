package srcs.chat.implem;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.BoolValue;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srcs.chat.proto.Chat.Chatter;
import srcs.chat.proto.ChatServerGrpc.ChatServerImplBase;

public class ChatImpl extends ChatServerImplBase {
	List<Channel> clients = new ArrayList<>();
	
	@Override
	public void subscribe(Chatter req, StreamObserver<BoolValue> res) {
		Channel client = ManagedChannelBuilder
				.forAddress(req.getHost(), req.getPort())
				.usePlaintext()
				.build();
		clients.add(client);
		res.onNext(BoolValue.of(true));
		res.onCompleted();
	}
}
