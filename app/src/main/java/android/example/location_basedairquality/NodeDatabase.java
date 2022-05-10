package android.example.location_basedairquality;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.Date;

@Database(entities = Node.class, version = 6)
public abstract class NodeDatabase extends RoomDatabase {
	
	private static NodeDatabase instance;
	
	private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
		
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);
			GenerateEntries generateEntries = new GenerateEntries(instance);
			generateEntries.GenerateEntries();
		}
	};
	
	public static synchronized NodeDatabase getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context.getApplicationContext(),
					NodeDatabase.class, "node_database")
					.fallbackToDestructiveMigration()
					.addCallback(roomCallback)
					.build();
		}
		return instance;
	}
	
	public abstract NodeDao nodeDao();
	
	private static class GenerateEntries {
		private NodeDao nodeDao;
		private GenerateEntries(NodeDatabase db) {
			nodeDao = db.nodeDao();
		}
		private void GenerateEntries() {
			new Thread(() -> {
				nodeDao.insert(new Node(12651651,
						128.3,
						12.3,
						222.5,
						22.3,
						23.333,
						"236541",
						107,
						new Date().getTime(), new Date().getTime()));
				nodeDao.insert(new Node(51654165, 22.2, 14.3, 822.5, 922.3, 43.333, "654654", 101, new Date().getTime(), new Date().getTime()));
				nodeDao.insert(new Node(98654451, 54.3, 22.3, 43.5, 422.3, 73.333, "23654651", 70, new Date().getTime(), new Date().getTime()));
			}).start();
			
		}
		
	}
}
