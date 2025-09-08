package pl.polsl.sikorfalf

import org.deidentifier.arx.ARXConfiguration
import org.deidentifier.arx.ARXAnonymizer
import org.deidentifier.arx.Data
import org.deidentifier.arx.AttributeType
import org.deidentifier.arx.DataHandle
import org.deidentifier.arx.criteria.DistinctLDiversity
import org.deidentifier.arx.criteria.KAnonymity
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.String

fun stablePseudo(original: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(original.toByteArray())
    return hash.take(4).joinToString("") { "%02x".format(it) }
}

fun initialHash(attributes: Map<String, Map<String, String>>, inputDatasetPath: String) {
    val csv = File("sandbox/csv/healthcare_dataset.csv")
    val csvOutput = File("sandbox/csv/dataset_hashed.csv")
    val handleCSV = csv.readLines()

    val attributesToHash = attributes.filter { it.value["type"] == "randomize_identifier" }.keys
    val header = handleCSV.first().split(",")
    csvOutput.printWriter().use { out ->
        out.println(header.joinToString(","))
        for (line in handleCSV.drop(1)) {
            val values = line.split(",")
            val row = header.mapIndexed { index, attr ->
                val value = values.getOrElse(index) { "" }
                if (attr in attributesToHash) stablePseudo(value) else value
            }
            out.println(row.joinToString(","))
        }
    }
}

fun main(trustLevel: Int): ByteArray {
    //Loading config from yaml file
    val yamlFile = File("sandbox/csv/data_policy.yaml")
    val yaml = Yaml()
    val configMap: Map<String, Any> = yaml.load(yamlFile.inputStream())
    val attributes = configMap["attributes"] as Map<String, Map<String, String>>

    //Hashing doctor, patient names
    initialHash(attributes, "sandbox/csv/healthcare_dataset.csv")

    //Use hashed csv file :)
    val csv = File("sandbox/csv/dataset_hashed.csv")
    val data = Data.create(csv, StandardCharsets.UTF_8, ',')

    //Set type of attr based yaml
    for ((attr, props) in attributes) {
        when (props["type"]) {
            "direct_identifier" -> data.definition.setAttributeType(attr, AttributeType.IDENTIFYING_ATTRIBUTE)
            "quasi_identifier" -> data.definition.setAttributeType(attr, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE)
            "sensitive" -> data.definition.setAttributeType(attr, AttributeType.SENSITIVE_ATTRIBUTE)
            "insensitive" -> data.definition.setAttributeType(attr, AttributeType.INSENSITIVE_ATTRIBUTE)
            "randomize_identifier" -> data.definition.setAttributeType(attr, AttributeType.INSENSITIVE_ATTRIBUTE)
        }
    }

    //Get yaml config for specific trust lvl :)
    val anonymPolicyAny = (configMap["anonymization_policy"] as Map<*, *>)["trust_levels"]
    val anonymPolicy = anonymPolicyAny as Map<Int, Map<String, Any>>

    val levelConfig = anonymPolicy[trustLevel] ?: throw IllegalStateException("Nie znaleziono konfiguracji dla trustLevel=$trustLevel")

    //Cofiguration k-anon and l-div
    val config = ARXConfiguration.create()
        val k = levelConfig["k"] as Int
        config.addPrivacyModel(KAnonymity(k))

        val l = levelConfig["l"] as Int
        attributes.forEach { (attr, props) ->
            if (props["type"] == "sensitive") {
                config.addPrivacyModel(DistinctLDiversity(attr, l))
            }
        }

    //Max % of records that can be deleted to
    config.setSuppressionLimit(levelConfig["suppression"] as Double)


    val generalizations = levelConfig["generalization"] as Map<String, String>
    val handle : DataHandle = data.handle

    for ((attr, gen) in generalizations) {
        val colIndex = handle.getColumnIndexOf(attr)
        val hierarchy = AttributeType.Hierarchy.create()
        val uniqueValues = mutableSetOf<String>()
        for (i in 0 until handle.numRows) uniqueValues.add(handle.getValue(i, colIndex))

        when (gen) {
            "floor" -> { // Room Number
                for (v in uniqueValues.map { it.toInt() }) {
                    val floor = v / 100
                    hierarchy.add(v.toString(), "Floor $floor", "*")
                }
            }

            "ABO" -> {
                for (v in uniqueValues) {
                    val group = when {
                        v.startsWith("A") && !v.startsWith("AB") -> "A"
                        v.startsWith("B") && !v.startsWith("AB") -> "B"
                        v.startsWith("AB") -> "AB"
                        else -> "O"
                    }
                    hierarchy.add(v, group)
                }
            }

            "remove", "*" -> {
                uniqueValues.forEach { hierarchy.add(it,"*", "*") }
            }

            "exact" -> {
                uniqueValues.forEach { hierarchy.add(it, it) }
            }

            "5y", "10y", "15y", "20y" -> {
                val step = when (gen) {
                    "5y" -> 5
                    "10y" -> 10
                    "15y" -> 15
                    "20y" -> 20
                    else -> 20
                }
                for (v in uniqueValues.map { it.toInt() }) {
                    val start = (v / step) * step
                    val end = start + step - 1
                    hierarchy.add(v.toString(), "$start-$end")
                }
            }

            "10000", "20000", "30000", "40000" -> {
                val step = when (gen) {
                    "10000" -> 10000.0
                    "20000" -> 20000.0
                    "30000" -> 30000.0
                    "40000" -> 40000.0
                    else -> 50000.0
                }
                for (v in uniqueValues.mapNotNull { it.toDoubleOrNull() }) {
                    val start = (v / step).toInt() * step
                    val end = start + step - 1
                    hierarchy.add(v.toString(), "${start.toInt()}-${end.toInt()}")
                }
            }

            "v" -> {
                for (v in uniqueValues) {
                    val group = when {
                        v.startsWith("A") && !v.startsWith("AB") -> "A"
                        v.startsWith("B") -> "B"
                        v.startsWith("AB") -> "AB"
                        else -> "O"
                    }
                    hierarchy.add(v, group)
                }
            }

            "year" -> {
                for (v in uniqueValues) {
                    val year = v.take(4) // YYYY
                    hierarchy.add(v, year)
                }
            }

            "year-month" -> {
                for (v in uniqueValues) {
                    val ym = v.take(7) // YYYY-MM
                    hierarchy.add(v, ym)
                }
            }

            "month" -> {
                for (v in uniqueValues) {
                    val parts = v.split("-")
                    if (parts.size >= 2) {
                        val ym = "${parts[0]}-${parts[1]}" // YYYY-MM
                        hierarchy.add(v, ym)
                    } else {
                        hierarchy.add(v, v, "*")
                    }
                }
            }

            "grouped" -> {
                uniqueValues.forEach { hierarchy.add(it, it, "*") }
            }

            else -> {
                uniqueValues.forEach { hierarchy.add(it, it, "*") }
            }
        }

        data.definition.setHierarchy(attr, hierarchy)

    }

    val anonymizer = ARXAnonymizer()
    val result = anonymizer.anonymize(data, config)

    println("Creating result file")
    result.output?.save("sandbox/csv/patients_anonymized.csv", ',') ?: run {
        println("Something goes wrong :/")
    }
    val resultFile = File("sandbox/csv/patients_anonymized.csv").readBytes()
    println("Complete, trust level: $trustLevel!")

    return resultFile
}