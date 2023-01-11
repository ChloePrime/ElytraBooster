package mod.chloeprime.elytrabooster.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.LivingEntity

object NullHumanoidModel :
    HumanoidModel<LivingEntity>(Dummies.EMPTY_ROOT) {

    @Suppress("UNCHECKED_CAST")
    fun <T: LivingEntity> instance() = NullHumanoidModel as HumanoidModel<T>
    override fun renderToBuffer(
        pPoseStack: PoseStack,
        pBuffer: VertexConsumer,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        // does nothing :)
    }

    private object Dummies {
        val EMPTY_PART = ModelPart(emptyList(), emptyMap())
        val EMPTY_ROOT = ModelPart(
            emptyList(), mapOf(
                "head" to EMPTY_PART,
                "hat" to EMPTY_PART,
                "body" to EMPTY_PART,
                "right_arm" to EMPTY_PART,
                "left_arm" to EMPTY_PART,
                "right_leg" to EMPTY_PART,
                "left_leg" to EMPTY_PART,
            )
        )
    }
}