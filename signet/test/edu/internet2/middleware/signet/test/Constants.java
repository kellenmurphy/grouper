/*--
$Id: Constants.java,v 1.8 2005-08-25 20:31:35 acohen Exp $
$Date: 2005-08-25 20:31:35 $

Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
Licensed under the Signet License, Version 1,
see doc/license.txt in this distribution.
*/
package edu.internet2.middleware.signet.test;

import java.util.Date;

/**
 * @author acohen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Constants
{
  public static final String SUBSYSTEM_ID
		= "testSubsystemId";
  public static final String SUBSYSTEM_NAME
		= "testSubsystemName";
  public static final String SUBSYSTEM_HELPTEXT
		= "testSubsystemHelptext";
  

  public static final String TREE_ID		= "testTreeId";
  public static final String TREE_NAME 	= "testTreeName";
  
  public static final String CHANGED_SUFFIX = "_CHANGED";
  
  public static final int MAX_CHOICE_SETS = 3;
  public static final int MAX_LIMITS			= 3;
  public static final int MAX_PERMISSIONS = 3;
  public static final int MAX_FUNCTIONS		= 3;
  public static final int MAX_CATEGORIES	= 3;
  public static final int MAX_SUBJECTS    = 3;
  public static final int MAX_TREE_DEPTH 	= 3;
  public static final int MAX_TREE_WIDTH 	= 3;
  
  public static final String DELIMITER = "_";
  
  public static final Date YESTERDAY
    = Common.getDate(-1);
  public static final Date TOMORROW
    = Common.getDate(1);
  public static final Date DAY_BEFORE_YESTERDAY
    = Common.getDate(-2);
  public static final Date DAY_AFTER_TOMORROW
    = Common.getDate(2);

  public static final boolean ASSIGNMENT_CANUSE = true;
  public static final boolean ASSIGNMENT_CANGRANT  = true;
  
  public static final boolean PROXY_CANUSE  = true;
  public static final boolean PROXY_CANEXTEND = true;
}
