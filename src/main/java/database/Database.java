package database;

import data.DataBaseEntry;
import utils.ResultHelper;

public interface Database {

    DataBaseEntry query();

    ResultHelper store(DataBaseEntry data);

}
