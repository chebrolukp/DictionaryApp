package com.example.dictionary.presentation.preview

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Font Scale 1.5x",
    showBackground = true,
    fontScale = 1.5f
)
@Preview(name = "Phone", showBackground = true, device = "spec:width=411dp,height=891dp")
@Preview(name = "Foldable", showBackground = true, device = "spec:width=673dp,height=841dp")
@Preview(name = "Tablet", showBackground = true, device = "spec:width=1280dp,height=800dp")

//combine all of the above
annotation class MultiPreview
