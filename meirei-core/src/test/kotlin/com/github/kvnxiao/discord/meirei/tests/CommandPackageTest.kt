/*
 *   Copyright (C) 2017 Ze Hao Xiao
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.kvnxiao.discord.meirei.tests

import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.tests.annotated.AnnotatedCommand
import com.github.kvnxiao.discord.meirei.tests.impl.CommandImpl
import com.github.kvnxiao.discord.meirei.tests.impl.MeireiTestImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandPackageTest {

    private val meirei = MeireiTestImpl()

    @Test
    fun testRegistryAware() {
        val id = "testRegistryAware.command.notAware"
        val regAwareId = "testRegistryAware.command.aware"
        val prefix = "/"
        val alias = "test"
        val regAwareAlias = "testAware"

        // Add commands
        meirei.addCommands(
            CommandPackage(CommandImpl(id), CommandProperties(id, setOf(alias), prefix), PermissionProperties()),
            CommandPackage(CommandImpl(regAwareId, true), CommandProperties(regAwareId, setOf(regAwareAlias), prefix), PermissionProperties(PermissionData()))
        )

        // Validate
        assertTrue(meirei.process("$prefix$alias"))
        assertTrue(meirei.process("$prefix$regAwareAlias"))
        assertFalse(meirei.process("not a real command"))

        // Cleanup
        assertTrue(meirei.registry.deleteCommand(id))
        assertTrue(meirei.registry.deleteCommand(regAwareId))
        assertTrue(meirei.registry.getAllCommands().isEmpty())
    }

    @Test
    fun testSubCommands() {
        val parentId = "testSubCommands.command.parent"
        val childAlpha = "testSubCommands.command.parent.childAlpha"
        val childBeta = "testSubCommands.command.parent.childBeta"
        val prefix = "/"
        val parentAlias = "parent"
        val childAlphaAlias = "alpha"
        val childBetaAlias = "beta"

        // Add commands
        meirei.addCommands(
            CommandPackage(CommandImpl(parentId), CommandProperties(parentId, setOf(parentAlias), prefix), PermissionProperties())
        )
        meirei.addSubCommands(parentId,
            CommandPackage(CommandImpl(childAlpha), CommandProperties(childAlpha, setOf(childAlphaAlias), prefix), PermissionProperties()),
            CommandPackage(CommandImpl(childBeta, true), CommandProperties(childBeta, setOf(childBetaAlias), prefix), PermissionProperties())
        )
        assertEquals(3, meirei.registry.getAllCommands().size)

        // Validate
        assertTrue(meirei.process("$prefix$parentAlias"))
        assertTrue(meirei.process("$prefix$parentAlias $childAlphaAlias"))
        assertTrue(meirei.process("$prefix$parentAlias $childBetaAlias"))

        // Cleanup
        assertTrue(meirei.registry.removeSubCommand(childAlpha, parentId))
        assertTrue(meirei.registry.getSubCommandRegistry(parentId) != null)
        assertTrue(meirei.registry.removeSubCommand(childBeta, parentId))
        assertTrue(meirei.registry.deleteCommand(parentId))
        assertTrue(meirei.registry.getAllCommands().isEmpty())
        assertTrue(meirei.registry.getSubCommandRegistry(parentId) == null)
    }

    @Test
    fun testAnnotatedCommands() {
        meirei.addAnnotatedCommands(AnnotatedCommand())

        // Validate
        assertTrue(meirei.process("/parent 123"))
        assertTrue(meirei.process("/parent child"))
        assertTrue(meirei.process("/parent child beta"))
        assertTrue(meirei.process("/parent child beta charlie"))
        assertTrue(meirei.process("/parent child third"))
        assertTrue(meirei.process("/parent child third fourth"))
    }

}