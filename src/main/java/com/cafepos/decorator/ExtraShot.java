package com.cafepos.decorator;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;

public final class ExtraShot extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.80);

    public ExtraShot(Product base) {
        super(base);
    }

 @Override public String name() { return base.name() + "+ Extra Shot"; }

    public Money price() {
        return (base instanceof Priced p
                ? p.price()
                : base.basePrice()).add(SURCHARGE);
    }
}

public final class OatMilk extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.50);

    public OatMilk(Product base) {
        super(base);
    }

 @Override public String name() { return base.name() + "+ Oat Milk"; }

    public Money price() {
        return (base instanceof Priced p
                ? p.price()
                : base.basePrice()).add(SURCHARGE);
    }
}

public final class Syrup extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.40);

    public Syrup(Product base) {
        super(base);
    }

 @Override public String name() { return base.name() + "+ Syrup"; }

    public Money price() {
        return (base instanceof Priced p
                ? p.price()
                : base.basePrice()).add(SURCHARGE);
    }
}

public final class SizeLarge extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.70);

    public SizeLarge(Product base) {
        super(base);
    }

 @Override public String name() { return base.name() + "(Large)"; }

    public Money price() {
        return (base instanceof Priced p
                ? p.price()
                : base.basePrice()).add(SURCHARGE);
    }
}