package com.example.testhive;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.Log;
import org.elastos.carrier.session.Manager;
import org.elastos.carrier.session.ManagerHandler;
import org.elastos.carrier.session.StreamState;
import org.junit.BeforeClass;

import static org.junit.Assert.assertNotNull;

public class ChannelTest {
    private static final String TAG = "ChannelTest";
    private static Synchronizer friendConnSyncher = new Synchronizer();
    private static Synchronizer commonSyncher = new Synchronizer();
    private static TestContext context = new TestContext();
    private static TestHandler handler = new TestHandler(context);

    private static Manager sessionManager;
    private static final SessionManagerHandler sessionHandler = new SessionManagerHandler();

    private static Carrier carrier;

    @BeforeClass
    public static void setUp() {
        try {
            TestOptions options = new TestOptions(context.getAppPath());
            carrier = Carrier.createInstance(options, handler);
            carrier.start(0);
            synchronized (carrier) {
                carrier.wait();
            }
            Log.i(TAG, "Carrier node is ready now");
            sessionManager = Manager.createInstance(carrier, sessionHandler);
            assertNotNull(sessionManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class LocalData {
        private StreamState mState;
        private int mCompleteStatus = 0;
        private int[] mChannelIDs = new int[128];
        private int mChannelIDCount = 0;
        private int[] mChannelErrorStates = new int[128];
    }

    static class SessionManagerHandler implements ManagerHandler {
        @Override
        public void onSessionRequest(Carrier carrier, String from, String sdp) {
            LocalData data = (LocalData)context.getExtra().getExtraData();
            if (data == null) {
                data = new LocalData();
                context.getExtra().setExtraData(data);
            }

            Log.d(TAG, String.format("Session Request from %s", from));
            synchronized (sessionHandler) {
                sessionHandler.notify();
            }
        }
    }

    static class TestHandler extends AbstractCarrierHandler {
        private TestContext mContext;

        TestHandler(TestContext context) {
            mContext = context;
        }

        @Override
        public void onReady(Carrier carrier) {
            synchronized (carrier) {
                carrier.notify();
            }
        }

        @Override
        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
            TestContext.Bundle bundle = mContext.getExtra();
            bundle.setRobotOnline(status == ConnectionStatus.Connected);
            bundle.setRobotConnectionStatus(status);
            bundle.setFrom(friendId);

            Log.d(TAG, "Robot connection status changed -> " + status.toString());
            friendConnSyncher.wakeup();
        }

        @Override
        public void onFriendAdded(Carrier carrier, FriendInfo info) {
            Log.d(TAG, String.format("Friend %s added", info.getUserId()));
            commonSyncher.wakeup();
        }

        @Override
        public void onFriendRemoved(Carrier carrier, String friendId) {
            Log.d(TAG, String.format("Friend %s removed", friendId));
            commonSyncher.wakeup();
        }
    }
}
