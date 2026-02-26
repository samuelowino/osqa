package com.owino;
/*
 * Copyright (C) 2026 Samuel Owino
 *
 * OSQA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSQA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSQA.  If not, see <https://www.gnu.org/licenses/>.
 */
import com.owino.core.OSQAModel;
import java.util.List;
public class TestData {
    public static final List<OSQAModel.OSQAModule> moduleOptions = List.of(
            new OSQAModel.OSQAModule(
                    "51fbd57d-a299-4891-8450-071b48b6a24d",
                    "Core Calendar and Navigation",
                    "Validates basic calendar rendering, navigation controls, and fundamental UI elements.",
                    "Critical",
                    List.of(
                            new OSQAModel.OSQATestCase(
                                    "0b8c4bf2-4590-4b01-bda2-cf7271a76789",
                                    "Smoke - Create Daily Task",
                                    "tc-smoke-001.json"),
                            new OSQAModel.OSQATestCase(
                                    "2a7d6c3e-1111-4d22-bda2-cf7271a7aaaa",
                                    "Smoke - Delete Daily Task",
                                    "tc-smoke-002.json"),
                            new OSQAModel.OSQATestCase(
                                    "3b9e8f4f-2222-4e33-bda2-cf7271a7bbbb",
                                    "Regression - Navigate Calendar Month",
                                    "tc-reg-001.json")
                    )
            ),
            new OSQAModel.OSQAModule(
                    "62acd12e-b111-4a99-9000-123456789abc",
                    "User Authentication",
                    "Validates login, logout, and session security workflows.",
                    "High",
                    List.of(
                            new OSQAModel.OSQATestCase(
                                    "4c1d2e3f-3333-4f44-bda2-cf7271a7cccc",
                                    "Smoke - Valid Login",
                                    "tc-auth-001.json"),
                            new OSQAModel.OSQATestCase(
                                    "5d2e3f4a-4444-4f55-bda2-cf7271a7dddd",
                                    "Regression - Invalid Password Login",
                                    "tc-auth-002.json"),
                            new OSQAModel.OSQATestCase(
                                    "6e3f4a5b-5555-4f66-bda2-cf7271a7eeee",
                                    "Security - Session Timeout",
                                    "tc-auth-003.json")
                    )
            ),
            new OSQAModel.OSQAModule(
                    "73bed23f-c222-4b77-8000-abcdefabcdef",
                    "Reporting Engine",
                    "Validates data aggregation, chart rendering, and export functionality.",
                    "Medium",
                    List.of(
                            new OSQAModel.OSQATestCase(
                                    "7f4a5b6c-6666-4f77-bda2-cf7271a7ffff",
                                    "Regression - Generate Summary Report",
                                    "tc-report-001.json"),
                            new OSQAModel.OSQATestCase(
                                    "c39e0d80-5c82-41f8-bdcd-dabbc237b1ae",
                                    "Smoke - Export Report PDF",
                                    "tc-report-002.json"),
                            new OSQAModel.OSQATestCase(
                                    "a2173ce9-dd4e-4e7c-80d4-60f1111182ca",
                                    "Performance - Large Dataset Report",
                                    "tc-report-003.json")
                    )
            )

    );
    public static final OSQAModel.OSQAModule firstIndexModule =
            new OSQAModel.OSQAModule(
            "62acd12e-b111-4a99-9000-123456789abc",
            "User Authentication",
            "Validates login, logout, and session security workflows.",
            "High",
            List.of(
                new OSQAModel.OSQATestCase(
                "4c1d2e3f-3333-4f44-bda2-cf7271a7cccc",
                "Smoke - Valid Login",
                "tc-auth-001.json"),
                new OSQAModel.OSQATestCase(
                "5d2e3f4a-4444-4f55-bda2-cf7271a7dddd",
                "Regression - Invalid Password Login",
                "tc-auth-002.json"),
                new OSQAModel.OSQATestCase(
                "6e3f4a5b-5555-4f66-bda2-cf7271a7eeee",
                "Security - Session Timeout",
                "tc-auth-003.json")
            ));
}
