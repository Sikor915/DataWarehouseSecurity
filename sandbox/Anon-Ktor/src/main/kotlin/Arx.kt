package pl.polsl.sikorfalf

import org.apache.hadoop.util.GenericsUtil.getClass
import org.deidentifier.arx.ARXConfiguration
import org.deidentifier.arx.ARXAnonymizer
import org.deidentifier.arx.Data
import org.deidentifier.arx.AttributeType
import org.deidentifier.arx.DataHandle
import org.deidentifier.arx.criteria.DistinctLDiversity
import org.deidentifier.arx.criteria.KAnonymity
import org.deidentifier.arx.criteria.LDiversity
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.StandardCharsets

fun main() {

    val yamlFile = File("../csv/data_policy.yaml")
    val yaml = Yaml()
    val configMap: Map<String, Any> = yaml.load(yamlFile.inputStream())
    val csv = File("../csv/healthcare_dataset.csv")

    val data = Data.create(csv, StandardCharsets.UTF_8, ',')


    val attributes = configMap["attributes"] as Map<String, Map<String, String>>
    for ((attr, props) in attributes) {
        when (props["type"]) {
            "direct_identifier" -> data.definition.setAttributeType(attr, AttributeType.IDENTIFYING_ATTRIBUTE)
            "quasi_identifier" -> data.definition.setAttributeType(attr, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE)
            "sensitive" -> data.definition.setAttributeType(attr, AttributeType.SENSITIVE_ATTRIBUTE)
            "insensitive" -> data.definition.setAttributeType(attr, AttributeType.INSENSITIVE_ATTRIBUTE)
        }
    }


    val anonymPolicyAny = (configMap["anonymization_policy"] as Map<*, *>)["trust_levels"]
    val anonymPolicy = anonymPolicyAny as Map<Int, Map<String, Any>>

    val trustLevel = 4
    val levelConfig = anonymPolicy[trustLevel] ?: throw IllegalStateException("Nie znaleziono konfiguracji dla trustLevel=$trustLevel")

    val config = ARXConfiguration.create()

        val k = levelConfig["k"] as Int
        config.addPrivacyModel(KAnonymity(k))


        val l = levelConfig["l"] as Int
        attributes.forEach { (attr, props) ->
            if (props["type"] == "sensitive") {
                config.addPrivacyModel(DistinctLDiversity(attr, l))
            }
        }

    config.setSuppressionLimit(levelConfig["suppression"] as Double)

    val generalizations = levelConfig["generalization"] as Map<String, String>
    val handle : DataHandle = data.handle

    for ((attr, gen) in generalizations) {
        val attrProps = attributes[attr] ?: continue
        val rowCount = handle.numRows
        val colIndex = handle.getColumnIndexOf(attr)

        when (attrProps["transformation"]) {
            "pseudonym_stable" -> {
                println("Pseudonimizacja stabilna dla $attr")

                val uniqueValues = mutableSetOf<String>()
                val colIndex = handle.getColumnIndexOf(attr)
                for (i in 0 until handle.numRows) {
                    uniqueValues.add(handle.getValue(i, colIndex))
                }


                val pseudoMap = mutableMapOf<String, String>()
                fun randomString(length: Int): String {
                    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
                    return (1..length).map { chars.random() }.joinToString("")
                }
                for (v in uniqueValues) pseudoMap[v] = randomString(8)


                val hierarchy = AttributeType.Hierarchy.create()
                for ((original, pseudo) in pseudoMap) {
                    hierarchy.add(original, pseudo, "*")
                }


                data.definition.setHierarchy(attr, hierarchy)
            }

            "Age" -> {
                val hierarchy = AttributeType.Hierarchy.create()
                val step = when (gen) {
                    "±1y" -> 1
                    "2y" -> 2
                    "5y" -> 5
                    "10y" -> 10
                    else -> 1
                }
                for (start in 0..120 step step) {
                    val end = start + step - 1
                    hierarchy.add(start.toString(), "$start-$end", "*")
                }
                data.definition.setHierarchy(attr, hierarchy)
            }

            "Room Number" -> {
                val hierarchy = AttributeType.Hierarchy.create()
                when (gen) {
                    "floor" -> for (room in 0..500) {
                        val floor = room / 100
                        hierarchy.add(room.toString(), "Floor $floor", "*")
                    }
                    "remove" -> for (room in 0..500) hierarchy.add(room.toString(), "*")
                    else -> for (room in 0..500) hierarchy.add(room.toString(), room.toString(), "*")
                }
                data.definition.setHierarchy(attr, hierarchy)
            }

            "Blood Type" -> {
                val hierarchy = AttributeType.Hierarchy.create()
                val allTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                when (gen) {
                    "*" -> allTypes.forEach { hierarchy.add(it, "*") }
                    "ABO" -> allTypes.forEach {
                        val group = when {
                            it.startsWith("A") && !it.startsWith("AB") -> "A"
                            it.startsWith("B") -> "B"
                            it.startsWith("AB") -> "AB"
                            else -> "O"
                        }
                        hierarchy.add(it, group, "*")
                    }
                    else -> allTypes.forEach { hierarchy.add(it, it, "*") }
                }
                data.definition.setHierarchy(attr, hierarchy)
            }

            else -> {

                val uniqueValues = mutableSetOf<String>()
                for (i in 0 until rowCount) uniqueValues.add(handle.getValue(i, colIndex))
                val hierarchy = AttributeType.Hierarchy.create()
                for (v in uniqueValues) hierarchy.add(v, "*")
                data.definition.setHierarchy(attr, hierarchy)
            }
        }
    }


    val anonymizer = ARXAnonymizer()
    val result = anonymizer.anonymize(data, config)


    result.output?.save("patients_anonymized.csv", ',') ?: run {
        println("Something goes wrong :/")
    }

    println("Complete, trust level: $trustLevel!")
}