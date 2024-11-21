plugins {
  id("voice.library")
  id("voice.compose")
  alias(libs.plugins.anvil)
}

anvil {
  generateDaggerFactories.set(true)
}

dependencies {
  implementation(projects.common)
  implementation(projects.strings)
  implementation(projects.playback)
  implementation(projects.data)
  implementation(projects.scanner)

  implementation(libs.androidxCore)
  implementation(libs.prefs.core)
  implementation(libs.materialDialog.core)
  implementation(libs.androidxCore)
  implementation(libs.material)
  implementation(libs.seismic)

  implementation(libs.dagger.core)

  testImplementation(libs.prefs.inMemory)
}
