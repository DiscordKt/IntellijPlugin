package me.jakejmattson.kutils.framework

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import java.awt.*
import java.io.File
import java.nio.file.Files
import javax.swing.*

private val chkProjectTemplate = JCheckBox("Project Template")
private val chkExtensiveExample = JCheckBox("Extensive Example")
private val buttons = listOf(chkProjectTemplate, chkExtensiveExample)

private val txtPackage = JTextField("me.your.organization.name")

class KUtilsWizard : ModuleBuilder() {
    override fun getModuleType() = object : ModuleType<KUtilsWizard>("KUtils") {
        override fun createModuleBuilder() = KUtilsWizard()
        override fun getName() = "KUtils"
        override fun getDescription() = "Create a project from a KUtils template."
        override fun getNodeIcon(b: Boolean) = AllIcons.General.Information!!
    }

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return arrayOf(createProjectSetupStep())
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        when (buttons.firstOrNull { it.isSelected }) {
            chkProjectTemplate -> generateProject(modifiableRootModel)
            chkExtensiveExample -> generateExample(modifiableRootModel)
        }
    }
}

private fun generateProject(modifiableRootModel: ModifiableRootModel) {

    val project = modifiableRootModel.project

    val basePath = project.basePath
    val packagePath = txtPackage.text.split(".").joinToString("/")
    val projectName = project.name.toLowerCase()

    val projectDir = File("$basePath/src/main/kotlin/$packagePath/$projectName")

    val commonFolders = listOf(
        File(projectDir.path + "/arguments"),
        File(projectDir.path + "/commands"),
        File(projectDir.path + "/preconditions"),
        File(projectDir.path + "/services")
    )

    val mainApp = File(projectDir.path + "/MainApp.kt")
    
    Files.createDirectories(projectDir.toPath())
    commonFolders.createDirectories()
    mainApp.createNewFile()
}

private fun List<File>.createDirectories() {
    this.forEach {
        Files.createDirectories(it.toPath())
    }
}

private fun generateExample(modifiableRootModel: ModifiableRootModel) {
    println("Generating Example")
}

private fun createProjectSetupStep() = object : ModuleWizardStep() {
    override fun getComponent() = JPanel().apply {
        layout = GridLayout(3, 1)

        addPanel {
            it.layout = GridLayout(2, 1)

            buttons.forEach { currentBox ->
                currentBox.addActionListener {
                    buttons.forEach {
                        if (it != currentBox)
                            it.isSelected = false
                    }
                }
                it.add(currentBox)
            }

            buttons.first().isSelected = true
        }

        addPanel {
            it.layout = FlowLayout()

            it.add(JLabel("GroupID: "))
            it.add(txtPackage)
        }
    }

    override fun updateDataModel() = Unit
}

private fun JPanel.addPanel(builder: (JPanel) -> Unit) {
    val panel = JPanel()
    builder.invoke(panel)
    add(panel)
}