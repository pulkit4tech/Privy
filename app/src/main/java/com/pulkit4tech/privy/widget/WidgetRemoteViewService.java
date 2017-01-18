package com.pulkit4tech.privy.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.utilities.PrivyProvider;

public class WidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewFactory();
    }

    class MyRemoteViewFactory implements RemoteViewsFactory{
        private Cursor data;
        @Override
        public void onCreate() {
            // Initialize
        }

        @Override
        public void onDataSetChanged() {
            if(data != null)
                data.close();

            final long identityToken = Binder.clearCallingIdentity();

            data = getContentResolver().query(
                    Uri.parse(PrivyProvider.URL),
                    null,
                    null,
                    null,
                    null
            );

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (data != null){
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(position == AdapterView.INVALID_POSITION ||
                    data == null || !data.moveToPosition(position))
                return null;

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);
            remoteViews.setTextViewText(R.id.widget_privy_name , data.getString(data.getColumnIndex(PrivyProvider.name)));
            remoteViews.setTextViewText(R.id.widget_lat, data.getString(data.getColumnIndex(PrivyProvider.lat)));
            remoteViews.setTextViewText(R.id.widget_lng, data.getString(data.getColumnIndex(PrivyProvider.lng)));

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            if(data != null && data.moveToPosition(i)){
                final int ID_COL = 0;
                return data.getLong(ID_COL);
            }
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
