package Coalesce.Framework.DataModel;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
-----------------------------------------------------------------------------*/

public enum ELanguages {
	
    English(0),
    French(1),
    Arabic(2),
    Spanish(3),
    Chinese(4),
    Russian(5),
    German(6),
    Korean(7),
    Japanese(8),
    Greek(9),
    Italian(10),
    Portuguese(11),
    Swedish(12),
    Romanian(13),
    Norwegian(14),
    Lithuanian(15),
    Hungarian(16),
    Polish(17),
    Dutch(18),
    Danish(19),
    Pashtu(20),
    HaitianCreole(21),
    Dari(22),
    Custom1(1001),
    Custom2(1002),
    Custom3(1003),
    Custom4(1004),
    Custom5(1005),
    Custom6(1007),
    Custom7(1007),
    Custom8(1008);
    
    private int value;    

    private ELanguages(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

};
