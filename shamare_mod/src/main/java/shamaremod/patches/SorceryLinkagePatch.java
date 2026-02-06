package shamaremod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.AbstractCreature;

import shamaremod.powers.SorceryVulnerable;
import shamaremod.powers.SorceryWeak;

/*
 * 这个补丁的作用是让游戏在检查一个生物是否有“Vulnerable”或“Weak”状态时，也把我们的自定义的SorceryVulnerable和SorceryWeak考虑进去。
 * 这样，任何依赖于hasPower("Vulnerable")或hasPower("Weak")的效果（比如战士的飞身踢）都能正确地与我们的新状态联动。
 * 注意，这个补丁只拦截了hasPower方法，如果你有其他直接检查power ID的代码（比如isVulnerable()），可能还需要额外的补丁来覆盖那些情况。
 */
@SpirePatch(clz = AbstractCreature.class, method = "hasPower")
public class SorceryLinkagePatch {

    @SpirePrefixPatch
    public static SpireReturn<Boolean> Prefix(AbstractCreature __instance, String targetID) {
        if (targetID.equals("Vulnerable")) {
            if (__instance.hasPower(SorceryVulnerable.POWER_ID)) {
                return SpireReturn.Return(true);
            }
        }
        if (targetID.equals("Weak")) {
            if (__instance.hasPower(SorceryWeak.POWER_ID)) {
                return SpireReturn.Return(true);
            }
        }
        return SpireReturn.Continue();
    }
}
