package shamaremod.cards.status;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import basemod.abstracts.CustomCard;
import shamaremod.helpers.IdHelper;
import shamaremod.helpers.ImageHelper;

public class ShadowDisaster extends CustomCard {

    public static final String ID = IdHelper.makePath("ShadowDisaster");
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = ImageHelper.getCardImgPath(CardType.STATUS, "ShadowDisaster");
    private static final int COST = -2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.STATUS;
    private static final CardColor COLOR = AbstractCard.CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private boolean has_triggered_this_turn = false;

    // 静态变量来跟踪当前的 magicNumber 加成
    private static int currentMagicNumberBonus = 0;

    public ShadowDisaster() {
        // 为了命名规范修改了变量名。这些参数具体的作用见下方
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 9 + currentMagicNumberBonus;
        this.magicNumber = this.baseMagicNumber;
        this.isEthereal = true;
    }

    public static int get_currentMagicNumberBonus() {
        return currentMagicNumberBonus;
    }

    public void set_magicnumber_by_hand(int num) {
        this.baseMagicNumber = num;
        this.magicNumber = num;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    private AbstractMonster choose_monster() {
        List<AbstractMonster> eligibleMonsters = new ArrayList<>();
        List<AbstractMonster> attackMonsters = new ArrayList<>();

        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                // 检查意图是否包含强化 (BUFF)
                // 排除 BUFF, ATTACK_BUFF, DEFEND_BUFF 以及可能的 UNKNOWN（特殊动作）
                boolean isBuffing = monster.intent == AbstractMonster.Intent.BUFF
                        || monster.intent == AbstractMonster.Intent.ATTACK_BUFF
                        || monster.intent == AbstractMonster.Intent.DEFEND_BUFF
                        || monster.intent == AbstractMonster.Intent.UNKNOWN;

                if (!isBuffing) {
                    eligibleMonsters.add(monster);
                    // 在不强化的敌人中，标记出正在攻击的
                    if (monster.intent == AbstractMonster.Intent.ATTACK
                            || monster.intent == AbstractMonster.Intent.ATTACK_DEBUFF
                            || monster.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
                        attackMonsters.add(monster);
                    }
                }
            }
        }

        // 优先从正在攻击且不强化的敌人中选择
        if (!attackMonsters.isEmpty()) {
            return attackMonsters.get(AbstractDungeon.cardRandomRng.random(attackMonsters.size() - 1));
        }
        // 如果没有攻击的，但有不强化的，则从中随机选
        if (!eligibleMonsters.isEmpty()) {
            return eligibleMonsters.get(AbstractDungeon.cardRandomRng.random(eligibleMonsters.size() - 1));
        }

        // 如果所有敌人都想强化，则返回 null，避免在该回合施加减力
        return null;
    }

    public void triggerWhenDrawn() {
        if (!this.has_triggered_this_turn) {
            // 随机选择一个【非强化意图】的敌人，并减少其力量
            AbstractMonster randomMonster = this.choose_monster();

            if (randomMonster != null) {
                // 施加debuff
                addToBot(new ApplyPowerAction(randomMonster, AbstractDungeon.player, new StrengthPower(randomMonster, -this.magicNumber), -this.magicNumber));
                if (!randomMonster.hasPower("Artifact")) {
                    addToBot(new ApplyPowerAction(randomMonster, AbstractDungeon.player, new GainStrengthPower(randomMonster, this.magicNumber), this.magicNumber));
                }
            }

            // 增加 magicNumber (即便该回合没找到合适的目标，灾祸依然会自我增生)
            this.incrementMagicNumberForAllShadowDisaster();
            this.has_triggered_this_turn = true;
        }
    }

    @Override
    public void triggerOnManualDiscard() {
        this.has_triggered_this_turn = false;
    }

    @Override
    public void onMoveToDiscard() {
        this.has_triggered_this_turn = false;
    }

    @Override
    public AbstractCard makeCopy() {
        return new ShadowDisaster();
    }

    private void incrementMagicNumberForAllShadowDisaster() {
        currentMagicNumberBonus += 3;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof ShadowDisaster) {
                card.baseMagicNumber += 3;
                card.magicNumber = card.baseMagicNumber;
                card.applyPowers();
            }
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof ShadowDisaster) {
                card.baseMagicNumber += 3;
                card.magicNumber = card.baseMagicNumber;
                card.applyPowers();
            }
        }
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            if (card instanceof ShadowDisaster) {
                card.baseMagicNumber += 3;
                card.magicNumber = card.baseMagicNumber;
                card.applyPowers();
            }
        }
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            if (card instanceof ShadowDisaster) {
                card.baseMagicNumber += 3;
                card.magicNumber = card.baseMagicNumber;
                card.applyPowers();
            }
        }
        for (AbstractCard card : AbstractDungeon.player.exhaustPile.group) {
            if (card instanceof ShadowDisaster) {
                card.baseMagicNumber += 3;
                card.magicNumber = card.baseMagicNumber;
                card.applyPowers();
            }
        }
    }

    // 新增方法：重置 currentMagicNumberBonus
    public static void resetMagicNumberBonus() {
        currentMagicNumberBonus = 0;
    }
}
