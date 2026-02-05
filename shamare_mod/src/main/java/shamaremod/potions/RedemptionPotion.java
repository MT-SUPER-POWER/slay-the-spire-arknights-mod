package shamaremod.potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;

import basemod.ReflectionHacks;
import shamaremod.helpers.IdHelper;
import shamaremod.powers.Namesis;

public class RedemptionPotion extends AbstractPotion {

    public static final String POTION_ID = IdHelper.makePath("RedemptionPotion");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(IdHelper.makePath("RedemptionPotion"));

    public RedemptionPotion() {
        super(potionStrings.NAME, IdHelper.makePath("RedemptionPotion"), AbstractPotion.PotionRarity.RARE, AbstractPotion.PotionSize.BOTTLE, AbstractPotion.PotionColor.NONE);
        this.labOutlineColor = Color.PURPLE;
        this.isThrown = false;
    }

    @Override
    public void initializeData() {
        ReflectionHacks.setPrivate(this, AbstractPotion.class, "containerImg", new Texture("shamaremod/images/potions/RedemptionPotion.png"));
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractPlayer p = AbstractDungeon.player;
        // 使用药水清空自身所有报应层数
        if (p.hasPower("ShamareKhas:Namesis")) {
            AbstractPower namesispower = p.getPower("ShamareKhas:Namesis");
            if (namesispower instanceof Namesis) {
                ((Namesis) namesispower).notifyDynamicCostCards_whenRemoved();
            }
            addToBot(new RemoveSpecificPowerAction(p, p, "ShamareKhas:Namesis"));
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        int the_potency = 0;
        return the_potency;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new RedemptionPotion();
    }
}
