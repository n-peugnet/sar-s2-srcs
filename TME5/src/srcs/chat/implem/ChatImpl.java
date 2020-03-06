package srcs.chat.implem;

import java.util.Hashtable;
import java.util.Map;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srcs.chat.proto.Chat.Chatter;
import srcs.chat.proto.Chat.ListChatter;
import srcs.chat.proto.Chat.Message;
import srcs.chat.proto.ChatClientGrpc;
import srcs.chat.proto.ChatClientGrpc.ChatClientFutureStub;
import srcs.chat.proto.ChatServerGrpc.ChatServerImplBase;

public class ChatImpl extends ChatServerImplBase {
	Map<String,Channel> clients = new Hashtable<>();
	
	@Override
	public void subscribe(Chatter req, StreamObserver<BoolValue> res) {
		Channel client = ManagedChannelBuilder
				.forAddress(req.getHost(), req.getPort())
				.usePlaintext()
				.build();
		clients.put(req.getPseudo(), client);
		res.onNext(BoolValue.of(true));
		res.onCompleted();
	}
	
	@Override
	public void send(Message msg, StreamObserver<Empty> res) {
		ChatClientFutureStub service;
		for(Channel c :clients.values()) {
			service = ChatClientGrpc.newFutureStub(c);
			service.newMessage(msg);
		}
	}
	
	@Override
	public void listChatter(Empty req, StreamObserver<ListChatter> res) {
		ListChatter lc = ListChatter.newBuilder().addAllValue(clients.keySet()).build();
		res.onNext(lc);
		res.onCompleted();
	}
	
	@Override
	public void unsubscribe(StringValue pseudo, StreamObserver<Empty> res) {
		clients.remove(pseudo.getValue());
		res.onNext(Empty.getDefaultInstance());
		res.onCompleted();
	}
}
