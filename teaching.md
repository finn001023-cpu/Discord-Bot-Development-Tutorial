Discord Bot Development Tutorial
Table of Contents

    Introduction

    Prerequisites

    Setting Up Your Bot

    Basic Bot Structure

    Event Handlers

    Commands

    Advanced Features

    Best Practices

Introduction

Discord bots are automated programs that interact with Discord servers. They can perform various tasks like moderating content, playing music, or providing information. This tutorial will guide you through creating your first Discord bot using Python.
Prerequisites

    Python 3.8 or higher

    A Discord account

    Basic Python knowledge

Setting Up Your Bot
1. Create a Discord Application

    Go to Discord Developer Portal

    Click "New Application"

    Name your application

    Go to the "Bot" section

    Click "Add Bot"

    Copy the bot token (keep this secret!)

2. Install Required Libraries
bash

pip install discord.py python-dotenv

Basic Bot Structure
Simple Bot Example (basic_bot.py)
python

import discord
from discord.ext import commands
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Bot setup
intents = discord.Intents.default()
intents.message_content = True
bot = commands.Bot(command_prefix='!', intents=intents)

@bot.event
async def on_ready():
    print(f'{bot.user} has connected to Discord!')
    await bot.change_presence(
        activity=discord.Activity(
            type=discord.ActivityType.watching,
            name="tutorial"
        )
    )

@bot.command(name='hello')
async def hello_command(ctx):
    """Responds with a greeting"""
    await ctx.send(f'Hello {ctx.author.mention}!')

@bot.command(name='ping')
async def ping_command(ctx):
    """Checks bot latency"""
    latency = round(bot.latency * 1000)
    await ctx.send(f'Pong! {latency}ms')

@bot.event
async def on_message(message):
    # Ignore messages from the bot itself
    if message.author == bot.user:
        return
    
    # Process commands
    await bot.process_commands(message)

# Run the bot
if __name__ == '__main__':
    bot.run(os.getenv('DISCORD_TOKEN'))

Environment File (.env)
env

DISCORD_TOKEN=your_bot_token_here

Event Handlers
Common Event Examples
python

import discord
from discord.ext import commands

bot = commands.Bot(command_prefix='!', intents=discord.Intents.all())

@bot.event
async def on_ready():
    print(f'Logged in as {bot.user}')

@bot.event
async def on_member_join(member):
    channel = member.guild.system_channel
    if channel:
        await channel.send(f'Welcome {member.mention}!')

@bot.event
async def on_member_remove(member):
    print(f'{member} left the server')

@bot.event
async def on_message_delete(message):
    # Log deleted messages
    print(f'Message deleted: {message.content}')

@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.CommandNotFound):
        await ctx.send("Command not found!")
    elif isinstance(error, commands.MissingPermissions):
        await ctx.send("You don't have permission to use this command!")

Commands
Basic Commands
python

import discord
from discord.ext import commands

bot = commands.Bot(command_prefix='!', intents=discord.Intents.default())

# Simple command
@bot.command()
async def info(ctx):
    """Displays bot information"""
    embed = discord.Embed(
        title="Bot Information",
        description="A simple Discord bot",
        color=0x00ff00
    )
    embed.add_field(name="Creator", value="Your Name", inline=True)
    embed.add_field(name="Server Count", value=len(bot.guilds), inline=True)
    await ctx.send(embed=embed)

# Command with arguments
@bot.command()
async def repeat(ctx, *, message):
    """Repeats the given message"""
    await ctx.send(message)

# Command with error handling
@bot.command()
@commands.has_permissions(manage_messages=True)
async def clear(ctx, amount: int = 5):
    """Clears specified number of messages"""
    if amount > 100:
        await ctx.send("Cannot delete more than 100 messages at once!")
        return
    
    deleted = await ctx.channel.purge(limit=amount + 1)
    await ctx.send(f'Deleted {len(deleted) - 1} messages', delete_after=5)

@clear.error
async def clear_error(ctx, error):
    if isinstance(error, commands.MissingPermissions):
        await ctx.send("You need manage messages permission!")

Slash Commands (Modern Approach)
python

import discord
from discord import app_commands

class MyBot(commands.Bot):
    def __init__(self):
        intents = discord.Intents.default()
        intents.message_content = True
        super().__init__(command_prefix='!', intents=intents)

    async def setup_hook(self):
        # Sync slash commands
        await self.tree.sync()
        print("Slash commands synced!")

bot = MyBot()

@bot.tree.command(name="greet", description="Greet a user")
@app_commands.describe(user="The user to greet")
async def greet_command(interaction: discord.Interaction, user: discord.Member):
    await interaction.response.send_message(f"Hello {user.mention}!")

@bot.tree.command(name="server_info", description="Get server information")
async def server_info_command(interaction: discord.Interaction):
    guild = interaction.guild
    embed = discord.Embed(title=f"{guild.name} Info", color=0x00ff00)
    embed.add_field(name="Members", value=guild.member_count)
    embed.add_field(name="Created", value=guild.created_at.strftime("%Y-%m-%d"))
    await interaction.response.send_message(embed=embed)

Advanced Features
Cog System (Modular Structure)

Create cogs/greetings.py:
python

import discord
from discord.ext import commands

class Greetings(commands.Cog):
    def __init__(self, bot):
        self.bot = bot

    @commands.Cog.listener()
    async def on_member_join(self, member):
        channel = member.guild.system_channel
        if channel:
            await channel.send(f'Welcome {member.mention}!')

    @commands.command()
    async def hello(self, ctx):
        await ctx.send(f'Hello {ctx.author.mention}!')

    @commands.command()
    async def goodbye(self, ctx):
        await ctx.send(f'Goodbye {ctx.author.mention}!')

async def setup(bot):
    await bot.add_cog(Greetings(bot))

Main bot file with cogs:
python

import discord
from discord.ext import commands
import os
from dotenv import load_dotenv

load_dotenv()

class MyBot(commands.Bot):
    def __init__(self):
        intents = discord.Intents.all()
        super().__init__(command_prefix='!', intents=intents)

    async def setup_hook(self):
        # Load cogs
        await self.load_extension('cogs.greetings')
        # Add more cogs here
        await self.tree.sync()

    async def on_ready(self):
        print(f'{self.user} is ready!')

bot = MyBot()

# Run the bot
if __name__ == '__main__':
    bot.run(os.getenv('DISCORD_TOKEN'))

Database Integration Example
python

import sqlite3
import discord
from discord.ext import commands

class DatabaseCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.conn = sqlite3.connect('bot_data.db')
        self.create_table()

    def create_table(self):
        cursor = self.conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS user_data (
                user_id INTEGER PRIMARY KEY,
                message_count INTEGER DEFAULT 0
            )
        ''')
        self.conn.commit()

    @commands.Cog.listener()
    async def on_message(self, message):
        if message.author.bot:
            return
        
        cursor = self.conn.cursor()
        cursor.execute('''
            INSERT OR REPLACE INTO user_data (user_id, message_count)
            VALUES (?, COALESCE((SELECT message_count FROM user_data WHERE user_id = ?), 0) + 1)
        ''', (message.author.id, message.author.id))
        self.conn.commit()

    @commands.command()
    async def stats(self, ctx):
        cursor = self.conn.cursor()
        cursor.execute('SELECT message_count FROM user_data WHERE user_id = ?', (ctx.author.id,))
        result = cursor.fetchone()
        count = result[0] if result else 0
        await ctx.send(f'You have sent {count} messages!')

async def setup(bot):
    await bot.add_cog(DatabaseCog(bot))

Best Practices
1. Error Handling
python

@bot.event
async def on_command_error(ctx, error):
    if isinstance(error, commands.CommandOnCooldown):
        await ctx.send(f"This command is on cooldown. Try again in {error.retry_after:.2f}s.")
    elif isinstance(error, commands.MissingRequiredArgument):
        await ctx.send("Missing required arguments!")
    elif isinstance(error, commands.BadArgument):
        await ctx.send("Invalid arguments provided!")
    else:
        print(f"Unhandled error: {error}")

# Command-specific error handling
@clear.error
async def clear_error(ctx, error):
    if isinstance(error, commands.MissingPermissions):
        await ctx.send("❌ You need manage messages permission!")

2. Security
python

# Use environment variables for tokens
import os
from dotenv import load_dotenv
load_dotenv()
TOKEN = os.getenv('DISCORD_TOKEN')

# Limit bot permissions in Discord Developer Portal
# Use specific intents instead of all intents

3. Performance
python

# Use cogs for modular code
# Implement command cooldowns
@bot.command()
@commands.cooldown(1, 30, commands.BucketType.user)
async def slow_command(ctx):
    await ctx.send("This command has a 30-second cooldown!")

# Use tasks for periodic operations
from discord.ext import tasks

@tasks.loop(minutes=30)
async def update_status():
    await bot.change_presence(activity=discord.Activity(type=discord.ActivityType.watching, name="the server"))

@update_status.before_loop
async def before_update_status():
    await bot.wait_until_ready()


Next Steps

    Explore the discord.py documentation: https://discordpy.readthedocs.io/

    Join the discord.py community: https://discord.gg/dpy

    Practice by adding more features:

        Music commands

        Moderation tools

        Games and entertainment

        API integrations

Remember to always test your bot thoroughly and follow Discord's Terms of Service and API guidelines







