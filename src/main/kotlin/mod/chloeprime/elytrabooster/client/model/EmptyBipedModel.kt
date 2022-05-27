package mod.chloeprime.elytrabooster.client.model

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.entity.model.BipedModel
import net.minecraft.client.renderer.model.ModelRenderer
import net.minecraft.entity.LivingEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.*

@OnlyIn(Dist.CLIENT)
class EmptyBipedModel<T : LivingEntity> : BipedModel<T>(0f) {

    override fun getHeadParts(): MutableIterable<ModelRenderer> {
        return Collections.emptyList()
    }

    override fun getBodyParts(): MutableIterable<ModelRenderer> {
        return Collections.emptyList()
    }

    override fun render(
        matrixStackIn: MatrixStack,
        bufferIn: IVertexBuilder,
        packedLightIn: Int,
        packedOverlayIn: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        // Do nothing :)
    }
}