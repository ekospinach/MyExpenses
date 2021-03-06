/*   This file is part of My Expenses.
 *   My Expenses is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   My Expenses is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Based on Financisto (c) 2010 Denis Solonenko, made available
 *   under the terms of the GNU Public License v2.0
*/

package org.totschnig.myexpenses.provider.filter;

import java.util.Arrays;
import java.util.LinkedList;
import org.totschnig.myexpenses.util.Utils;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseArray;

public class WhereFilter {

  public static final String LIKE_ESCAPE_CHAR = "\\";

  private SparseArray<Criteria> criterias= new SparseArray<Criteria>();
  private final LinkedList<String> sorts = new LinkedList<String>();

  public WhereFilter() {
  }

  public WhereFilter(SparseArray<Parcelable> sparseArray) {
    for (int i = 0; i < sparseArray.size(); i++) {
      put(sparseArray.keyAt(i),(Criteria) sparseArray.valueAt(i));
    }
  }

  public WhereFilter asc(String column) {
    sorts.add(column + " asc");
    return this;
  }

  public WhereFilter desc(String column) {
    sorts.add(column + " desc");
    return this;
  }

  public String getSelectionForParents(String tableName) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0, nsize = criterias.size(); i < nsize; i++) {
      Criteria c = criterias.valueAt(i);
      if (c != null) {
        if (sb.length() > 0) {
          sb.append(" AND ");
        }
        sb.append(c.getSelectionForParents(tableName));
      }
    }
    return sb.toString().trim();
  }
  public String getSelectionForParts(String tableName) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0, nsize = criterias.size(); i < nsize; i++) {
      Criteria c = criterias.valueAt(i);
      if (c != null) {
        if (sb.length() > 0) {
          sb.append(" AND ");
        }
        sb.append(c.getSelectionForParts(tableName));
      }
    }
    return sb.toString().trim();
  }

  public String[] getSelectionArgs(boolean queryParts) {
    String[] args = new String[0];
    for(int i = 0, nsize = criterias.size(); i < nsize; i++) {
      Criteria c = criterias.valueAt(i);
      if (c!=null) {
        String critArgs[] = c.getSelectionArgs();
        if (queryParts || c.shouldApplyToParts()) {
          critArgs = Utils.joinArrays(critArgs, critArgs);
        }
        //we need to double each criteria since it is applied to parents and parts
        args = Utils.joinArrays(args, critArgs);
      }
    }
    return args;
  }

  public Criteria get(int id) {
    return criterias.get(id);
  }

  public void put(int id, Criteria criteria) {
    criterias.put(id, criteria);
  }

  public void remove(int id) {
    criterias.remove(id);
  }

  public void clear() {
    criterias.clear();
    sorts.clear();
  }

  public static WhereFilter empty() {
    return new WhereFilter();
  }

  public String getSortOrder() {
    StringBuilder sb = new StringBuilder();
    for (String o : sorts) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(o);
    }
    return sb.toString();
  }

  public void resetSort() {
    sorts.clear();
  }

  public boolean isEmpty() {
    return criterias.size()==0;
  }

  public static enum Operation {
    NOPE(""), EQ("=?"), NEQ("!=?"), GT(">?"), GTE(">=?"), LT("<?"), LTE("<=?"), BTW(
        "BETWEEN ? AND ?"), ISNULL("is NULL"), LIKE("LIKE ? ESCAPE '" + LIKE_ESCAPE_CHAR + "'"),
    IN(null);

    public final String op;

    private Operation(String op) {
      this.op = op;
    }

    public String getOp(int length) {
      switch(this) {
        case IN:
          StringBuilder sb = new StringBuilder();
          sb.append("IN (");
          for (int i = 0; i < length-1; i++) {
            sb.append("?,");
          }
          sb.append("?)");
          return sb.toString();
        default:
          return op;
      }
    }
  }

  public SparseArray<Criteria> getCriteria() {
    return criterias;
  }

}
