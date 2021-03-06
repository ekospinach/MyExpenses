package org.totschnig.myexpenses.test.activity.myexpenses;

import org.totschnig.myexpenses.activity.MyExpenses;
import org.totschnig.myexpenses.test.activity.MyActivityTest;
import org.totschnig.myexpenses.ui.FragmentPagerAdapter;
import org.totschnig.myexpenses.R;

import com.robotium.solo.Solo;

import android.support.v4.view.ViewPager;


/**
 * This class runs first and tests if the database is initialized,
 * welcome dialog shown and adapter set up
 * 
 * @author Michael Totschnig
 */
public class A_InstallTest extends MyActivityTest<MyExpenses> {

  ViewPager mPager;
  
  public A_InstallTest() {
    super(MyExpenses.class);
  }

  public void setUp() throws Exception {
    super.setUp();
    mActivity = getActivity();
    mSolo = new Solo(getInstrumentation(), mActivity);
    mPager = (ViewPager) mActivity.findViewById(R.id.viewpager);
  }
  public void testDatabaseIsCreatedAndWelcomeDialogIsShown() {
    dismissWelcomeScreen();
    assertTrue("Empty view not shown", mSolo.searchText(mContext.getString(R.string.no_expenses)));
    mAdapter = (FragmentPagerAdapter) mPager.getAdapter();
    assertTrue(mAdapter != null);
    assertEquals(mAdapter.getCount(),1);
  }
}
