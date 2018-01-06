package dbms;

import java.io.File;

public class Drop extends Statement {

	public Drop(String[] querySplited, String currentDataBase, Object returnObject) {
		super(querySplited, currentDataBase, returnObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object excute() throws Exception {
		if (super.querySplited[1].equalsIgnoreCase("database")) {
			final String temp =System.getProperty("user.dir") + System.getProperty("file.separator") + "databases" + System.getProperty("file.separator") + querySplited[2];
			final File f = new File(temp);
			final boolean result = deleteDir(f);
			super.returnObject = result;
			if(temp.equalsIgnoreCase(currentDataBase)){
				super.currentDataBase = null;
			}
			return (result);
		} else if (super.querySplited[1].equalsIgnoreCase("table")) {

			final File f = new File(super.currentDataBase + System.getProperty("file.separator") + super.querySplited[2] + ".xml");
			final boolean result = deleteDir(f);
			super.returnObject = result;
			return (result);

		}
		return false;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			final String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				final boolean success = deleteDir(new File(dir, children[i]));

				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
